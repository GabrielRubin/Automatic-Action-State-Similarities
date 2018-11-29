/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package competition.richmario;

import competition.richmario.shapings.Shaping;
import org.apache.commons.math3.util.Pair;

import javax.swing.plaf.nimbus.State;
import java.util.*;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 *
 * @author timbrys
 */
public class QLHash {

    protected double alpha;
    protected double gamma;
    protected double lambda;

    protected Shaping init;

    protected HashMap<Long, Double>      weights;
    protected HashMap<Long, Integer>     weightsCount;
    protected HashMap<Long, StateAction> weightsStates;
    protected HashMap<Long, Double>      es;

    //protected StateAction     bestQState;
    //protected ArrayList<Long> bestQStateValueKeys;

    private Long currBestIndex = null;

    //protected ArrayList<StateAction> states;

    public QLHash(double alpha, double gamma, double lambda, Shaping init){
        this.weights       = new HashMap<Long, Double>();
        this.weightsCount  = new HashMap<>();
        this.weightsStates = new HashMap<>();
        this.es            = new HashMap<Long, Double>();
        //this.bestQState    = null;
        //this.bestQStateValueKeys = new ArrayList<>(100);

        this.alpha = alpha;
        this.gamma = gamma;
        this.lambda = lambda;

        this.init = init;
    }

    public void reset(){
        resetEs();
        resetWeights();
    }

    public void resetWeights(){

        this.weights = new HashMap<Long, Double>();
        this.weightsCount = new HashMap<>();
    }

    public void resetEs(){
        this.es = new HashMap<Long, Double>();
    }

    public double getValue(StateAction features){
        if(!weights.containsKey(features.key())){

            weights.put(features.key(), init.potential(features));
            //weightsStates.put(features.key(), features);
        }
        return weights.get(features.key());
    }

    public double getValueNoUpdate(StateAction features){
        if(!weights.containsKey(features.key())){
            return init.potential(features);
        } else {
            return weights.get(features.key());
        }
    }

    public void setValue(StateAction features, double value){

        weights.put(features.key(), value);
    }

    public boolean hasEntry(StateAction features){
        return weights.containsKey(features.key());
    }

    public void update(double delta){
        Long f;
        double currBestVal = 0.0;
        for(Iterator<Long> it = es.keySet().iterator(); it.hasNext(); ){

            f = it.next();
            double val = weights.get(f);
            weights.put(f, val + (alpha * delta * es.get(f)));

            //int newVisitCount = 1;
            //if(weightsCount.containsKey(f))
            //    newVisitCount = weightsCount.get(f) + 1;
            //weightsCount.put(f, newVisitCount);
            //if(!this.bestQStateValueKeys.contains(f) && val > currBestVal) {
//
            //    this.currBestIndex = f;
            //    currBestVal = val;
            //}
        }
    }

    public void decay(){
        Map.Entry<Long, Double> f;
        double e;
        for(Iterator<Map.Entry<Long, Double>> it = es.entrySet().iterator(); it.hasNext(); ){
            f = it.next();
            e = f.getValue()*gamma*lambda;
            if(e < 0.001){
                it.remove();
            } else {
                es.put(f.getKey(), e);
            }
        }
    }

    private StateAction lastTraceState;

    public void setTraces(StateAction features){
        es.put(features.key(), 1.0);
        lastTraceState = features;
    }

    public StateAction GetLastTraceState() {

        return lastTraceState;
    }


    //public StateAction GetMostVisitedState() {
//
    //    if(bestQStateValueKeys.size() >= 100) {
    //        Collections.sort(bestQStateValueKeys,
    //                (o1, o2) -> weights.get(o1).compareTo(weights.get(o2)));
    //        if(bestQStateValueKeys.get(0) < weights.get(currBestIndex)) {
//
    //            bestQState = weightsStates.get(currBestIndex);
    //        }
    //        else {
//
    //            bestQState = weightsStates.get(bestQStateValueKeys.get(0));
    //        }
    //    }
    //    else if(currBestIndex != null && !bestQStateValueKeys.contains(currBestIndex)) {
//
    //        bestQState = weightsStates.get(currBestIndex);
    //        //if(weightsStates.get(currBestIndex).key() != currBestIndex) {
    //        //    System.out.println("oops!");
    //        //    StateAction sa = weightsStates.get(currBestIndex);
    //        //    System.out.println(sa.key());
    //        //}
    //    }
    //    return bestQState;
    //}
//
    //public void SaveState() {
//
    //    if(bestQStateValueKeys.size() >= 100)
    //        bestQStateValueKeys.remove(0);
//
    //    bestQStateValueKeys.add(currBestIndex);
    //}

    public void setTracesSimilarity(StateAction features, Double similarity){
        Double ess = es.get(features.key());
        if(ess == null) {
            es.put(features.key(), similarity);
        } else if(ess != null && ess < similarity) {
        }
    }

    public int getSize(){
        return weights.size();
    }
}
