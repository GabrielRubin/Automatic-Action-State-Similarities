package competition.richmario;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.nd4j.linalg.api.ndarray.*;
import org.nd4j.linalg.factory.Nd4j;
import org.deeplearning4j.nn.modelimport.keras.*;
import org.deeplearning4j.nn.multilayer.*;
import org.nd4j.linalg.io.ClassPathResource;

import java.util.Queue;


public class JavaKerasInterface {

    private static ComputationGraph m_model;

    public JavaKerasInterface(String modelName) {

        try {
            String simpleMlp = new ClassPathResource(modelName).getFile().getPath();
            int[] shape = new int[5];
            m_model = KerasModelImport.importKerasModelAndWeights(simpleMlp);
        }catch(Exception e) {

            System.out.println("wow! Error: " + e.getMessage());
        }
    }

    public double GetSimilarity(StateAction stateA, StateAction stateB) {

        INDArray inputA = Nd4j.create(StateAction.normalize(stateA.getState()));
        INDArray inputB = Nd4j.create(StateAction.normalize(stateB.getState()));

        INDArray outputA = m_model.outputSingle(inputA);
        INDArray outputB = m_model.outputSingle(inputB);

        double distance = outputA.distance2(outputB) / 32.0;

        return 1.0 - distance;
    }

    public double[] GetEmbeddings(StateAction a, StateAction b) {

        INDArray input = Nd4j.create(new double[][] { StateAction.normalize(a.getState()), StateAction.normalize(b.getState()) });

        double[][] result = m_model.outputSingle(input).toDoubleMatrix();

        return new double[]{ result[0][0], result[0][1], result[1][0], result[1][1] };
    }

    public double[] GetEmbedding(StateAction a) {

        INDArray input = Nd4j.create(StateAction.normalize(a.getState()));

        return m_model.outputSingle(input).toDoubleVector();
    }

    //public double[] GetSimilarities(StateAction stateA, Queue<StateAction> comparingStates) {
//
    //    double[][] inputValues = new double[comparingStates.size() + 1][10];
    //    inputValues[0] = StateAction.normalize(stateA.getState());
    //    int i = 1;
    //    for(StateAction s : comparingStates) {
//
    //        inputValues[i] = StateAction.normalize(s.getState());
    //        i++;
    //    }
    //    INDArray   input  = Nd4j.create(inputValues);
    //    INDArray[] output = m_model.output(input);
    //    double[] result = new double[comparingStates.size()];
    //    for(int j = 0; j < result.length; j++) {
//
    //        double distance = output[0].distance2(output[j]) / 32.0;
//
    //        result[j] = 1.0 - distance;
    //    }
    //    return result;
    //}

}
