package encoder.parallelization.tasks;

import encoder.parallelization.ChunkContext;
import encoder.parallelization.Dependency;
import encoder.processing.Node;
import encoder.processing.interfaces.IHuffmanNode;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created with IntelliJ IDEA.
 * User: Stefan
 * Date: 11.09.12
 * Time: 14:19
 * Generate a tree based on a given array. The tree will be generated based on the huffman algorithm.
 */
public class BuildTreeTask extends TaskBase {


    /**
     * Basic Constructor for BuildTreeTask
     *
     * @param context    Object with data to work with
     * @param dependency Adds a dependency for this task
     */
    public BuildTreeTask(ChunkContext context, Dependency dependency) {
        super(context);
        addDependency(dependency);
    }

    /**
     * Basic Constructor for BuildTreeTask
     *
     * @param context    Object with data to work with
     */
    public BuildTreeTask(ChunkContext context) {
        super(context);
    }

    /**
     * Generate a tree based on a given array. The tree will be generated based on the huffman algorithm.
     *
     * @throws IOException
     */
    @Override
    protected void runInternal() throws IOException {
        //Generate list
        List<IHuffmanNode> list = new LinkedList<IHuffmanNode>();
        IHuffmanNode[] array = _context.getNodeArray();
        for (IHuffmanNode node : array) {
            if (node.getFrequency() != 0) {
                list.add(node);
            }
        }

        //Add all array elements to a priority queue
        //the priority Queue works with the comparedTo Method of the @IHuffmanNode class
        PriorityQueue<IHuffmanNode> pQ = new PriorityQueue<IHuffmanNode>(list);

        //while more then 1 element is in the queue
        while (pQ.size() > 1) {
            //Create new parentNode
            Node parent = new Node(null);
            //add child to parent
            parent.setLeft(pQ.poll());
            parent.setRight(pQ.poll());
            //return parent to Queue
            //two child nodes are now combined in one parent
            pQ.add(parent);
        }

        //Return last node in queue, the root node.
        _context.setRootNode(pQ.poll());
    }
}
