package uk.me.mjt.ch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DirectedEdge {
    public static final long PLACEHOLDER_ID = -123456L;
    private static final int PREMAKE_UNCONTRACTED_THRESHOLD = 10;

    public final long edgeId;
    public final Node from;
    public final Node to;
    public final int driveTimeMs;

    // Parameters for graph contraction:
    public final DirectedEdge first;
    public final DirectedEdge second;
    public final int contractionDepth;
    private final UnionList<DirectedEdge> uncontractedEdges;
    
    public DirectedEdge(long edgeId, Node from, Node to, int driveTimeMs) {
        this(edgeId,from,to,driveTimeMs,null,null);
    }

    public DirectedEdge(long edgeId, Node from, Node to, int driveTimeMs, DirectedEdge first, DirectedEdge second) {
        Preconditions.checkNoneNull(from, to);
        Preconditions.require(edgeId>0||edgeId==PLACEHOLDER_ID, driveTimeMs >= 0);
        if (edgeId>0 && first!=null && second!=null) {
            // If this check starts failing, your edge IDs for shortcuts probably start too low.
            Preconditions.require(edgeId > first.edgeId, edgeId>second.edgeId);
        }
        this.edgeId = edgeId;
        this.from = from;
        this.to = to;
        this.driveTimeMs = driveTimeMs;
        this.first = first;
        this.second = second;
        if (first == null && second == null) {
            contractionDepth = 0;
            uncontractedEdges = null;
        } else if (first != null && second != null){
            contractionDepth = Math.max(first.contractionDepth, second.contractionDepth)+1;
            uncontractedEdges = new UnionList<>(first.getUncontractedEdges(),second.getUncontractedEdges());
        } else {
            throw new IllegalArgumentException("Must have either both or neither child edges set. Instead had " + first + " and " + second);
        }
    }
    
    public boolean isShortcut() {
        return (contractionDepth != 0);
    }

    public List<DirectedEdge> getUncontractedEdges() {
        if (!isShortcut()) {
            return Collections.singletonList(this);
        } else {
            return uncontractedEdges;
        }
    }
    
    /*public List<DirectedEdge> getUncontractedEdges() {
        ArrayList<DirectedEdge> result = new ArrayList<>(4000);
        appendUncontractedEdges(result);
        result.trimToSize();
        return Collections.unmodifiableList(result);
    }
    
    private void appendUncontractedEdges(List<DirectedEdge> toAppend) {
        if (!isShortcut()) {
            toAppend.add(this);
        } else {
            first.appendUncontractedEdges(toAppend);
            second.appendUncontractedEdges(toAppend);
        }
    }*/
    
    public DirectedEdge cloneWithEdgeId(long edgeId) {
        return new DirectedEdge(edgeId, from, to, driveTimeMs, first, second);
    }
    
    public String toString() {
        return from.nodeId+"--"+driveTimeMs+"("+contractionDepth+")-->"+to.nodeId;
    }
    
}
