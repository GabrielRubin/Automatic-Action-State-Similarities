package competition.richmario.experiment;

import competition.richmario.*;
import competition.richmario.agents.QLambdaAgent;
import org.apache.commons.math3.util.Pair;
import org.apache.commons.math3.ml.distance.*;

import java.util.*;

import javax.swing.plaf.nimbus.State;

/**
 * Created by user on 15/05/2017.
 */
public class SimilarityManager {

    //private static HashMap<Pair<Long,Long>, Double> m_map;

    public static AutoEncoderCommunicator  m_communicator;
    public static JavaKerasInterface       m_interface;
    private static List<double[]>          m_embedStates;
    private static List<StateAction>       m_states;
    private static DistanceMeasure         m_distanceCalc;
    private static HashMap<Long, double[]> m_embeddings;
    private static StateAction m_lastState = null;

    /**
     *
     * @param state represents the state:
     *      [0] can jump?   : 0-1
     *      [1] on ground?  : 0-1
     *      [2] able to shoot?  : 0-1
     *      [3] current direction   : 0-8
     *              direction codes:    [4 = stays in place]
     *
     *                  6   7   8
     *
     *                  3   4   5
     *
     *                  0   1   2
     *
     *      [4] close enemies yes or no in 8 directions : 0-255
     *      returns one or sum of few of the following numbers:
     *          1   64  8
     *          4   M   32
     *          2   128 16
     *
     *      result can also be analyzed as byte:
     *          LSB     [0]     up-left
     *                  [1]     down-left
     *                  [2]     left
     *                  [3]     up-right
     *                  [4]     down-right
     *                  [5]     right
     *                  [6]     up
     *          MSB     [7]     down
     *
     *      [5] mid-range enemies yes or no in 8 directions : 0-255
     *          <same encoding as [4]]>
     *
     *      [6] far enemies yes or no in 8 directions   : 0-255
     *          <same encoding as [4]]>
     *
     *      [7] obstacles in front  : 0-15
     *          the sum of one or few of the following numbers:
     *          0   -   no obstacles in mario immediate surrounding
     *          1   -   mario touching an obstacle (standing on it not considered 'touching')
     *          2   -   obstacle in the very close surrounding of mario (below him not considered)
     *          4   -   obstacle in the close surrounding of mario (below him not considered)
     *          8   -   obstacle in the surrounding of mario, but not very close (below him not considered)
     *
     *                    8
     *                    4
     *                    2
     *                  M 1
     *
     *          for instance, in case there is an obstacle very close to Mario, the value will be 2.
     *          in case both Mario touching an obstacle and there is another one not very close to him
     *          the value will be 9 (the sum of 1 and 8).
     *
     *      [8] closest enemy x : 0-21
     *      [9] closest enemy y : 0-21
     *
     * @param action represent the action:
     *      ** NOTICE: the 'run' key makes mario to shoot fireballs as well, if he in the right mode (white clothes)
     *
     *      0   -   do nothing
     *      1   -   press key:  'go left'
     *      2   -   press key:  'go right'
     *      3   -   press key:  'jump'
     *      4   -   press keys: 'go left' + 'jump'
     *      5   -   press keys: 'go right' + 'jump'
     *      6   -   press key:  'run'
     *      7   -   press keys: 'go left' + 'run'
     *      8   -   press keys: 'go right'  + 'run'
     *      9   -   press keys: 'jump' + 'run'
     *      10  -   press keys: 'go left' + 'jump' + 'run'
     *      11  -   press keys: 'go right' + 'jump' + 'run'
     *
     * @return list of (<state, action>, similarity_factor) pairs
     */
    public static List<Pair<StateAction, Double>> getSimilarityRecords(int[] state, int action) {

        ArrayList<Pair<StateAction, Double>> similarityRecords = new ArrayList<>();
        if (AgentType.Similarities != SimpleExperiment.activeAgentType) {
            return similarityRecords;
        }

        // *** YOUR CODE HERE **********************************************************************

        // *** END OF YOUR CODE ********************************************************************

        return similarityRecords;
    }

    public static List<Pair<StateAction, Double>> GetSimilarities(StateAction lastState, double[] embeddings, QLambdaAgent agent) {

        ArrayList<Pair<StateAction, Double>> similarityRecords = new ArrayList<>();
        if(!m_states.contains(lastState)) {

            m_embedStates.add(new double[]{embeddings[2], embeddings[3]});
            if (m_embedStates.size() > 50)
                m_embedStates.remove(0);
            m_states.add(lastState);
            if (m_states.size() > 50)
                m_states.remove(0);
            //agent.SaveState();
        }

        double[] result = new double[] { embeddings[0], embeddings[1] };

        for(int i = 0; i < m_embedStates.size(); i++) {

            double[] sEmb = m_embedStates.get(i);

            double[] key  = new double[] { result[0], result[1], sEmb[0], sEmb[1] };

            double distance = m_distanceCalc.compute(result, sEmb);
            distance /= 5;
            distance = Math.min(distance, 1.0);
            double similarity = 1.0 - distance;

            //double similarity = 1.0 - Math.min(CosineSimilarity(result, sEmb), 1.0);
            //if(similarity < 0.01)
            //    similarity = 0;

            StateAction newState = m_states.get(i);
            newState.setAction(lastState.getAction());
            similarityRecords.add(new Pair<>(newState, similarity));
        }
        return similarityRecords;
    }

    public static double CosineSimilarity(double[] vectorA, double[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    //public static List<Pair<StateAction, Double>> GetSimilaritiesViaCommunicator(StateAction state, QLambdaAgent agent) {
//
    //    if(m_communicator == null) {
//
    //        m_communicator = new AutoEncoderCommunicator();
    //    }
//
    //    if(m_embedStates == null) {
//
    //        m_embedStates = new ArrayList<>(100);
    //    }
//
    //    if(m_states == null) {
//
    //        m_states = new ArrayList<>(100);
    //    }
//
    //    if(m_distanceCalc == null) {
//
    //        m_distanceCalc = new EuclideanDistance();
    //    }
//
    //    if (AgentType.Similarities != SimpleExperiment.activeAgentType) {
    //        return new ArrayList<>();
    //    }
//
    //    StateAction lastState = agent.GetMostVisitedState();
    //    if(lastState == null)
    //        return new ArrayList<>();
//
    //    double[] embeddings = m_communicator.GetEmbeddingsUDP(state.toString(), lastState.toString());
//
    //    return GetSimilarities(lastState, embeddings, agent);
    //}

    public static List<Pair<StateAction, Double>> GetSimilaritiesViaDL4J(StateAction state, QLambdaAgent agent) {

        if(m_interface == null) {

            m_interface = new JavaKerasInterface("competition/richmario/marioStateEncoder.h5");
        }

        if(m_embedStates == null) {

            m_embedStates = new ArrayList<>(5);
        }

        if(m_states == null) {

            m_states = new ArrayList<>(5);
        }

        if(m_distanceCalc == null) {

            m_distanceCalc = new EuclideanDistance();
        }

        if(m_embeddings == null) {

            m_embeddings = new HashMap<>();
        }

        if (AgentType.Similarities != SimpleExperiment.activeAgentType) {
            return new ArrayList<>();
        }

        //StateAction mostVisitedState = agent.GetMostVisitedState();
        //if(mostVisitedState == null)
        //    return new ArrayList<>();

        //TRACE
        m_lastState = agent.GetLastTraceState();

        //STATE HISTORY
        //if(m_lastState == null) {
////
        //    m_lastState = state;
        //    return new ArrayList<>();
        //}

        long currStateKey         = state.key();
        long mostVisitedStateKey  = m_lastState.key();
        boolean containsCurrState = m_embeddings.containsKey(currStateKey);
        boolean containsMVState   = m_embeddings.containsKey(mostVisitedStateKey);

        double[] embeddings;
        if(!containsCurrState && !containsMVState) {
            embeddings = m_interface.GetEmbeddings(state, m_lastState);
            m_embeddings.put(currStateKey, new double[] { embeddings[0], embeddings[1] });
            m_embeddings.put(mostVisitedStateKey, new double[] { embeddings[2], embeddings[3] });
        }
        else if(!containsCurrState && containsMVState) {

            double[] firstPart  = m_interface.GetEmbedding(state);
            double[] secondPart = m_embeddings.get(mostVisitedStateKey);
            embeddings = new double[] { firstPart[0], firstPart[1], secondPart[0], secondPart[1] };
            m_embeddings.put(currStateKey, new double[] { firstPart[0], firstPart[1] });
        }
        else if(containsCurrState && !containsMVState) {

            double[] firstPart  = m_embeddings.get(currStateKey);
            double[] secondPart = m_interface.GetEmbedding(m_lastState);
            embeddings = new double[] { firstPart[0], firstPart[1], secondPart[0], secondPart[1] };
            m_embeddings.put(mostVisitedStateKey, new double[] { secondPart[0], secondPart[1] });
        }
        else {

            double[] firstPart  = m_embeddings.get(currStateKey);
            double[] secondPart = m_embeddings.get(mostVisitedStateKey);
            embeddings = new double[] { firstPart[0], firstPart[1], secondPart[0], secondPart[1] };
        }

        List<Pair<StateAction, Double>> result = GetSimilarities(m_lastState, embeddings, agent);

        //m_lastState = state;

        return result;
    }

    // *** YOUR CODE HERE **********************************************************************
    // Here you can add custom STATIC help functions, if needed

    // *** END OF YOUR CODE ********************************************************************
}
