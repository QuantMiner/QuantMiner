package src.solver;

import java.util.*;
import src.apriori.*;
import src.database.*;
import src.geneticAlgorithm.*;

public class GMeansClusterer {

    //parametrized constructor
    public GMeansClusterer(ArrayList assocRules, StandardParametersQuantitative input_parametresReglesQuantitatives){

        rules = assocRules;

        m_parametresReglesQuantitatives = input_parametresReglesQuantitatives;

        kMeansClusterer = new KMeansClusterer(assocRules, m_parametresReglesQuantitatives);

        listOfCentroids = new ArrayList<Centroid>();
        centroidClusters = new HashMap<Centroid, ArrayList<AssociationRule>>();

    }

    public ArrayList applyGMeansAlgo(){
        //is 10 a good max amount? or take as input?
        return applyGMeansAlgo(15);
    }

    //maybe do some initializations outside loops etc?
    public ArrayList applyGMeansAlgo(int maxKClusterAmt){

        System.out.println("maxKClusterAmt: " + maxKClusterAmt);

        int i=0;
        int totalNumKClusters = 0;

        //kMeansClusterer = new KMeansClusterer(rules);
        listOfCentroids = kMeansClusterer.applyKMeansAlgo(1);

        while(i < listOfCentroids.size() && totalNumKClusters < maxKClusterAmt){

            System.out.println("G MEANS LIST OF CENTROIDS SIZE: " + listOfCentroids.size());

            System.out.println("current centroid G MEANS: " + listOfCentroids.get(i));

            //int numKClusters = 1;

            //initial iteration: k means for k = 1. Generate random clusters
            //listOfCentroids = kMeansClusterer.applyKMeansAlgo(numKClusters, null);

            //apply on specific data? HOW TO APPLY TO SPECIFIC DATA...

            //listOfCentroids = kMeansClusterer.applyKMeansAlgo(numKClusters);

            //centroidClusters will be the entirety of the data set, since all points map to the one cluster
            centroidClusters = kMeansClusterer.getCentroidClusters();

            //uncomment to verify centroidClusters has all the points (since one key, and all points as values for the key)
            //System.out.println(centroidClusters.size());
            Centroid oldCentroid = listOfCentroids.get(i);
            ArrayList<AssociationRule> initialDataPoints = centroidClusters.get(oldCentroid);

            System.out.println("initialDataPoints: " + initialDataPoints);

            //initial data points can be null....
            if(initialDataPoints == null){
                //go to next iteration in loop
                i++;
                continue;
            }

            //for when other times, when data points restricted:
            KMeansClusterer newKMeansClusterer = new KMeansClusterer(initialDataPoints, m_parametresReglesQuantitatives);

            //get 2 clustersr for data points
            //ArrayList<Centroid> newListOfCentroids = newKMeansClusterer.applyKMeansAlgo(numKClusters + 1, listOfCentroids);
            ArrayList<Centroid> newListOfCentroids = newKMeansClusterer.applyKMeansAlgo(2);

            Map<Centroid, ArrayList<AssociationRule>> newCentroidClusters = newKMeansClusterer.getCentroidClusters();

            System.out.println("newCentroidClusters.keySet(): " + newCentroidClusters.keySet());

            System.out.println("newCentroidClusters.keySet().size(): " + newCentroidClusters.keySet().size());

            System.out.println("newListOfCentroids.size(): " + newListOfCentroids.size());

            Centroid c1 = new Centroid();
            Centroid c2 = new Centroid();;

            if(newListOfCentroids.size() == 2){

                c1 = newListOfCentroids.get(0);
                c2 = newListOfCentroids.get(1);
            

                if(c1 != null && c2 != null){
                    // Vector will be <c1 coordinates> + t<direction vector>. Represent as ArrayList: {c1 coordinates, direction vector}
                   // ArrayList<ArrayList> vectorAL = createVector(c1, c2);
                    ArrayList<ArrayList> vectorAL = createVector(c1, c2);

                    //project points in initialDataPoints onto vectorAL
                   /* ArrayList<AssociationRule> projectedPoints = projectPoints(initialDataPoints, vectorAL);

                    ArrayList<AssociationRule> standardizedPoints = standardizePoints(projectedPoints);*/

                    ArrayList<Centroid> projectedPoints = projectPoints(initialDataPoints, vectorAL);

                    ArrayList<Centroid> standardizedPoints = standardizePoints(projectedPoints);

                    ArrayList<Double> andersonDarlingResult = andersonDarlingTest(standardizedPoints);

                    boolean isNonCritical = isNonCritical(andersonDarlingResult);

                   // if(andersonDarlingResult is in range of noncritical vals at confidence level phi ie isNonCritical == true)
                    if(isNonCritical){
                        //accept original center
                        i++;
                    }else{
                        //ie isNonCritical == false
                        //replace original center with c1 and c2 in listOfCentroids
                        listOfCentroids.remove(i);
                        listOfCentroids.add(i, c2);
                        listOfCentroids.add(i, c1);
                        //added a cluster
                        totalNumKClusters++;
                        //i stays the same

                        centroidClusters.remove(oldCentroid);
                        centroidClusters.put(c1, newCentroidClusters.get(c1));
                        centroidClusters.put(c2, newCentroidClusters.get(c2));
                    }

                }

            }else{
                //throw error
                System.out.println("ERROR: newListOfCentroids.size() != 2");
                //IMPORTANT: 
                //note: this can happen if the new list of centroids removes a centroid that had no points mapped to it. What do I do here? keep the one centroid??
                //move on?
                i++;
            }
    
        }


        listOfCentroids = cleanUpRepeatRules(listOfCentroids);

        return listOfCentroids;

    }

    private ArrayList<Centroid> cleanUpRepeatRules(ArrayList<Centroid> listOfCentroids){

        ArrayList<Centroid> cleanedUpList = listOfCentroids;

        double ruleProximity = testRuleProximities(listOfCentroids);
        System.out.println("(double)(ruleProximity / 50): " + (double)(ruleProximity / 50));

        for(int i=0; i<listOfCentroids.size(); i++){

            Centroid centroidConsidered = listOfCentroids.get(i);

            //these are the decimal representations... need to put to percentage.
            System.out.println("m_parametresReglesQuantitatives.m_fMinSupp: " + m_parametresReglesQuantitatives.m_fMinSupp);
            System.out.println("m_parametresReglesQuantitatives.m_fMinConf: " + m_parametresReglesQuantitatives.m_fMinConf);

            //fix supp and conf for centroid before applying this!
           /* if(centroidConsidered.getSupport() < m_parametresReglesQuantitatives.m_fMinSupp || centroidConsidered.getConfidence() < m_parametresReglesQuantitatives.m_fMinConf ){
                listOfCentroids.remove(i);
                continue;
            }*/

            for(int j=0; j<listOfCentroids.size(); j++){

                Centroid centroidInComparison = listOfCentroids.get(j);

                double distanceBtwnCentroids = kMeansClusterer.calculateEuclideanDistance(centroidConsidered.getCoordinates(), centroidInComparison.getCoordinates());
                if(j != i){
                    System.out.println(distanceBtwnCentroids);

                    //use euclidean distance to remove 'similar' rules. need to base on average euclidean dist? IOW, how to find how far to consider 'distinct'?
                    if(distanceBtwnCentroids < (double)(ruleProximity / 50)){
                        //question: remove centroids with numOcc == 0 first?
                        centroidConsidered.setNumOccurrences(centroidConsidered.getNumOccurrences() + centroidInComparison.getNumOccurrences());

                        centroidConsidered.setSupport(centroidConsidered.getSupport() + centroidInComparison.getSupport());

                        centroidConsidered.setConfidence(centroidConsidered.getConfidence() + centroidInComparison.getConfidence());

                        //listOfCentroids.remove(j);
                        cleanedUpList.remove(j);

                        j--;
                    }
                    //continue;
                }

            }

        }

        return cleanedUpList;

    }
        
        

    public double testRuleProximities(ArrayList<Centroid> centroidList){

        double avgDist = 0;

        int centListSize = centroidList.size();

        for(int i=0; i<centListSize; i++){
            Centroid centConsidered1 = (Centroid)centroidList.get(i);

            ArrayList listOfIntervalsA = centConsidered1.getCoordinates();

            for(int j=i+1; j<centListSize; j++){

                Centroid centConsidered2 = (Centroid)centroidList.get(j);
                ArrayList listOfIntervalsB = centConsidered2.getCoordinates();

                double doubleDistance = 0;

                //list of intervals has intervals ([, ], [, ], [, ]) etc in assocation rule from left to right
                for(int a = 0; a < listOfIntervalsA.size(); a++){
                    
                    //need CHECKING for if list of intervals' sizes not the same
                    //checks if double or float
                    double coordinate = (double)Math.abs((float)listOfIntervalsA.get(a) - (float)listOfIntervalsB.get(a));

                    double coordSquared = coordinate*coordinate;
                    doubleDistance += coordSquared;
                
                }

                double distance = Math.sqrt(doubleDistance);
                avgDist += distance;


            }

        }

        //sum of nums from 1 to n is 0.5(n)(n-1)
        avgDist /= ((centListSize)*(centListSize - 1) * 0.5);

        return avgDist;

    }


    // AL representation of vector returned will be <c1 coordinates> + t<direction vector>. Represent as ArrayList: {c1 coordinates, direction vector}
    private ArrayList<ArrayList> createVector(Centroid c1, Centroid c2){

        ArrayList vector = new ArrayList();

        ArrayList c1Coords = c1.getCoordinates();
        ArrayList c2Coords = c2.getCoordinates();

        vector.add(c1Coords);
        //vector.add(c1);

        ArrayList<Float> directionVector = new ArrayList<Float>();

        if(c1Coords.size() == c2Coords.size()){
            for(int i=0; i<c2Coords.size(); i++){
                float difference = (Float)c2Coords.get(i) - (Float)c1Coords.get(i);
                directionVector.add(difference);
            }

            System.out.println("directionVector: " + directionVector);

        }else{
            System.out.println("ERROR: c1Coords.size() != c2Coords.size()");
        }

        vector.add(directionVector);
    

        return vector;

    }

    private ArrayList<Centroid> projectPoints(ArrayList<AssociationRule> dataPoints, ArrayList<ArrayList> vectorAL){

        //is this ok as an AL of centroids? b/c they are a bit easier to deal with.
        ArrayList<Centroid> projectedPoints = new ArrayList<Centroid>();
        //ArrayList<Centroid> projectedPoints = new ArrayList<Centroid>();

        //vectorAL.get(0) is c1. vectorAL.get(1) is direction vector
        for(int i=0; i<dataPoints.size(); i++){
            AssociationRule rule = dataPoints.get(i);
            //System.out.println("rule: " + rule);
            //maybe move getQuantIntervals?

            //each elt of quantIntervalsForRule is a tuple {lower bd, upper bd}
            ArrayList<float[]> quantIntervalsForRule = kMeansClusterer.getQuantIntervals(rule);
            ArrayList coordinatesForRule = new ArrayList();
            for(int j=0; j<quantIntervalsForRule.size(); j++){
                coordinatesForRule.add(quantIntervalsForRule.get(j)[0]);
                coordinatesForRule.add(quantIntervalsForRule.get(j)[1]);
            }

            //System.out.println("coordinatesForRule: " + coordinatesForRule);

            //Make a centroid for the rule, with the coordinates being coordinatesForRule
            Centroid centroidForRule = new Centroid(coordinatesForRule);

            //seems inefficient to make new centroid out of c1 coords... find a better way

            ArrayList c1Coords = (ArrayList)(vectorAL.get(0));
            Centroid c1copy = new Centroid(c1Coords);

            ArrayList vectorToRulePt = createVector(c1copy, centroidForRule);

            System.out.println("vectorToRulePt.get(0): " + vectorToRulePt.get(0));
            System.out.println("vectorToRulePt.get(1): " + vectorToRulePt.get(1));

            //distance: dot product of vectorAL and vectorToRulePt, over magnitude of vector AL squared
            float distance = 0;

            float dotProduct = 0;


            ArrayList v1Direction = (ArrayList)(vectorAL.get(1));
            ArrayList v2Direction = (ArrayList)(vectorToRulePt.get(1));

            System.out.println("v1Direction: " + v1Direction);
            System.out.println("v2Direction: " + v2Direction);

            //NaN if init to 0, but otherwise get NaN error...
            float magnitudeV1Squared = 0;


            if(v1Direction.size() == v2Direction.size()){

                for(int a=0; a< v1Direction.size(); a++){

                    float v1Coord = (float)v1Direction.get(a);
                    float v2Coord = (float)v2Direction.get(a);

                    dotProduct += (v1Coord * v2Coord);
                    magnitudeV1Squared += (v2Coord * v2Coord);

                }

                if(magnitudeV1Squared > 0){
                    distance = (float)(dotProduct / magnitudeV1Squared);
                }
                

                 
                
            }else{
                System.out.println("ERROR: v1Direction.size() != v2Direction.size()");
            }

            System.out.println("distance: " + distance);

            ArrayList projectedCoords = new ArrayList();

            ArrayList direction = (ArrayList)(vectorAL.get(1));

            //new point is vectorAL with t = distance
            if(direction.size() == c1Coords.size()){
                for(int k=0; k < c1Coords.size(); k++){
                    float newCoord = (float)c1Coords.get(k) + distance*(float)direction.get(k); 
                    projectedCoords.add(newCoord);
                }
            }else{
                System.out.println("ERROR: direction.size() != c1Coords.size()");
            }

            if(projectedCoords.size() > 0){
                Centroid projectedCentroid = new Centroid(projectedCoords);
                System.out.println("projectedCentroid: " + projectedCentroid);

                //if projectedPoints is made into an AL of centroids
                projectedPoints.add(projectedCentroid);
            }


        }

        System.out.println("projectedPoints: " + projectedPoints);

        return projectedPoints;
    }

    private ArrayList<Centroid> standardizePoints(ArrayList<Centroid> projectedPoints){
        
        ArrayList<Centroid> standardizedPoints = new ArrayList<Centroid>();

        ArrayList<Float> mean = getMean(projectedPoints);

        ArrayList<Double> stdDev = getStdDev(mean, projectedPoints);

        Centroid currCentroid;

        int numPoints = projectedPoints.size();
        
        for(int i=0; i<numPoints; i++){

            currCentroid = projectedPoints.get(i);
            //System.out.println("currCentroid: " + currCentroid);
            ArrayList<Float> coords = currCentroid.getCoordinates();

            //ok to have double coords?
            ArrayList<Double> standardizedCoords = new ArrayList<Double>();

            //System.out.println("coords: " + coords);
            for(int j=0; j<coords.size(); j++){
                //issue: stdDev = 0; divide by 0 --> get float NaN issue
                standardizedCoords.add((coords.get(j) - mean.get(j)) / stdDev.get(j));
            }

            System.out.println("standardizedCoords: " + standardizedCoords);
            Centroid standardizedCent = new Centroid(standardizedCoords);
            standardizedPoints.add(standardizedCent);
        }

        System.out.println("standardizedPoints: " + standardizedPoints);
        return standardizedPoints;
    }

    private ArrayList<Float> getMean(ArrayList<Centroid> projectedPoints){
        ArrayList<Float> mean = new ArrayList<Float>();

        float tempSum = 0;

        Centroid currCentroid;

        int numPoints = projectedPoints.size();
        System.out.println("numPoints: " + numPoints);
        
        for(int i=0; i<numPoints; i++){
            currCentroid = projectedPoints.get(i);
            //System.out.println("currCentroid: " + currCentroid);
            ArrayList<Float> coords = currCentroid.getCoordinates();
            //System.out.println("coords: " + coords);
            for(int j=0; j<coords.size(); j++){

                //System.out.println("coords.get(j): " + coords.get(j));

                //System.out.println("mean.get(j): " + mean.get(j));
                //mean.add((float)0);

                if(mean != null && mean.size() > j){
                    tempSum = mean.get(j) + coords.get(j);
                    mean.set(j, tempSum);
                }else{
                    tempSum = coords.get(j);
                    mean.add(tempSum);
                }

            }
        }

        for(int i=0; i<mean.size(); i++){
            if(numPoints != 0){
                float sum = mean.get(i);
                float meanVal = (sum / numPoints );
                mean.set(i, meanVal);
            }else{
                System.out.println("ERROR: numPoints == 0");
                mean.set(i, (float)0);
            }

        }

        System.out.println("mean: " + mean);
        return mean;
    }

    private ArrayList<Double> getStdDev(ArrayList<Float> mean, ArrayList<Centroid> projectedPoints){

        ArrayList<Double> stdDev = new ArrayList<Double>();
        ArrayList<Double> sdSum = new ArrayList<Double>();

        Centroid currCentroid;

        double tempSum = 0;

        int numPoints = projectedPoints.size();
        
        for(int i=0; i<numPoints; i++){
            currCentroid = projectedPoints.get(i);
            //System.out.println("currCentroid: " + currCentroid);
            ArrayList<Float> coords = currCentroid.getCoordinates();
            //System.out.println("coords: " + coords);
            for(int j=0; j<coords.size(); j++){
                if(sdSum != null && sdSum.size() > j){
                    tempSum = sdSum.get(j) + (Math.pow(coords.get(j) - mean.get(j), 2) / numPoints);
                    sdSum.set(j, tempSum);
                }else{
                    tempSum = (Math.pow(coords.get(j) - mean.get(j), 2) / numPoints);
                    sdSum.add(tempSum);
                }
            }

        }

        for(int i=0; i<sdSum.size(); i++){
            stdDev.add(Math.sqrt((double)sdSum.get(i)));
        }

        System.out.println("stdDev: " + stdDev);

        return stdDev;

    }

    //put anderson darling test in different file??
    private  ArrayList<Double> andersonDarlingTest(ArrayList<Centroid> standardizedPoints){
        //double andersonDarlingResult = 0.0;

        int numPoints = standardizedPoints.size();

        //ISSUE: CD NT BE BETWEEN 0 AND 1, BUT THIS IS NOT THE CASE CURRENTLY
        ArrayList<ArrayList<Double>> cumulativeDistribution = applyCumulativeDistributionFxn(standardizedPoints);

        ArrayList<Double> aSqZ = new ArrayList<Double>();

        for(int i=0; i<cumulativeDistribution.size(); i++){
            ArrayList<Double> CDforCoord = cumulativeDistribution.get(i);
            int CDCoordsize = CDforCoord.size();
            for(int j=0; j<CDCoordsize; j++){
                //check the indexing
                //double addedVal = ((2*j - 1) * (Math.log(CDforCoord.get(j)) + Math.log(CDforCoord.get(CDCoordsize - 1 - j))) - CDCoordsize);

                //issue: getting NaN when second log input is negative,,, and log only defined for x > 0
                double addedVal = ((2*(j + 1) - 1) * (Math.log(CDforCoord.get(j)) + Math.log(1 - CDforCoord.get(CDCoordsize - 1 - j))));
                System.out.println("addedVal: " + addedVal);

                //need to check for NaN -- but how to avoid with NaN not impacting calculations??
                if(!Double.isNaN(addedVal)){ 
                    if(aSqZ.size() > i){
                        aSqZ.set(i, aSqZ.get(i) + addedVal);
                    }else{
                        aSqZ.add(addedVal);
                    }
                }else{
                    if(aSqZ.size() > i){
                        aSqZ.set(i, 0.0);
                    }else{
                        aSqZ.add(0.0);
                    }
                }

            }

            if(aSqZ.size() > i){
                aSqZ.set(i, (-1 * aSqZ.get(i) / CDCoordsize) - CDCoordsize);
            }
            

        }

        System.out.println("aSqZ: " + aSqZ);

        ArrayList<Double> aSqZStar = new ArrayList<Double>();

        for(int i=0; i<aSqZ.size(); i++){
            ArrayList<Double> CDforCoord = cumulativeDistribution.get(i);
            int n = CDforCoord.size();

            aSqZStar.add(aSqZ.get(i) * (1 + 4/(n - 25/(Math.pow(n, 2)))));

        }

        System.out.println("aSqZStar: " + aSqZStar);

        //return A^2*(z)
       // return andersonDarlingResult;
        return aSqZStar;
    }

    private ArrayList<ArrayList<Double>> applyCumulativeDistributionFxn(ArrayList<Centroid> standardizedPoints){
        
        //has the cumulative distribution of each x_i, for each x_i in the coordinates of the centroid
        ArrayList<ArrayList<Double>> cumulativeDistribution = new ArrayList<ArrayList<Double>>();

        //ArrayList with elts that are ALs of all the vals for each coordinate x_i
        ArrayList<ArrayList> ALsOfCoords = new ArrayList<ArrayList>();

        //float vs double coords ... NT deal with both cases
        for(int i=0; i<standardizedPoints.size(); i++){
            Centroid thisCentroid = standardizedPoints.get(i);
            ArrayList coords = thisCentroid.getCoordinates();
            for(int j=0; j<coords.size(); j++){
                if(ALsOfCoords.size() > j){
                    ArrayList thisCoordAL = ALsOfCoords.get(j);
                    thisCoordAL.add(coords.get(j));
                    ALsOfCoords.set(j, thisCoordAL);
                }else{
                    ArrayList thisCoordAL = new ArrayList();
                    thisCoordAL.add(coords.get(j));
                    ALsOfCoords.add(thisCoordAL);
                }
            }
        }

        //System.out.println("ALsOfCoords: " + ALsOfCoords);

        for(int i=0; i<ALsOfCoords.size(); i++){
            ArrayList thisCoordAL = ALsOfCoords.get(i);

            //make all values their absolute values, because we need the magnitude, and will be squared in computations
            for(int j=0; j<thisCoordAL.size(); j++){
                thisCoordAL.set(j, Math.abs((double)thisCoordAL.get(j)));
            }

            Collections.sort(thisCoordAL);

            //transform into a hashmap with key = x value, value = num. occurrences
            HashMap<Object, Integer> thisCoordHM = new HashMap<Object, Integer>();
            for(int j=0; j<thisCoordAL.size(); j++){
                Object thisCoord = thisCoordAL.get(j);
                if(thisCoordHM.containsKey(thisCoord)){
                    thisCoordHM.put(thisCoord, (int)thisCoordHM.get(thisCoord) + 1);
                    //remove double occurrence?

                }else{
                    thisCoordHM.put(thisCoord, 1);
                }
            }

            ArrayList<Double> thisCoordCDF = new ArrayList<Double>();
            double cumulativeDistVal = 0.0;

            //ISSUE: CUMULATIVE DISTRIBUTION SHOULD BE BETWEEN 0 AND 1, THIS IS NOT THE CASE!
            for(int j=0; j<thisCoordAL.size(); j++){

                //throw conversion error if applicable?
                /*double coordDoubleVal = (double)thisCoordAL.get(i);
                int numOccurences = thisCoordHM.get(thisCoordAL.get(i));*/
                double coordDoubleVal = (double)thisCoordAL.get(j);

               // System.out.println("coordDoubleVal: " + coordDoubleVal);
                int numOccurences = thisCoordHM.get(thisCoordAL.get(j));

                //System.out.println("numOccurences: " + numOccurences);

                double power = (-0.5 * coordDoubleVal * coordDoubleVal);
                double ePower = Math.exp(power);
              //  System.out.println("ePower: " + ePower);
                double sqrt2Pi = Math.sqrt(2 * Math.PI);

                //double addedVal = numOccurences * (ePower / sqrt2Pi);

                //for summation / Riemann sum, multiply height (f(x) val) times change in x.
                double fXVal = (ePower / sqrt2Pi);

                //change in x value
                double dx = 0.0;

                if(j == 0){
                    dx = coordDoubleVal;
                }else{
                    dx = coordDoubleVal - (double)thisCoordAL.get(j-1);
                }

                double addedVal = 0.0;

                System.out.println("fXVal: " + fXVal);

                addedVal = (fXVal * dx);

                //double addedVal = (Math.exp(-0.5 * (Math.pow(coordDoubleVal, 2))) / Math.sqrt(2 * Math.PI));

                System.out.println("addedVal: " + addedVal);

                //THIS IS WRONG B/C IT ONLY RELIES ON THE CURR NUM, NOT HOW IT RELATES TO THE OTHER NUMS!!!
                // java.lang.Math.exp(double a) returns Euler's number e raised to the power of a double value
                cumulativeDistVal += addedVal;
                //cumulativeDistVal = addedVal;
                
                
                //need to make sure doesn't increment each time if numOccurrences > 1.
            
                if(!Double.isNaN(cumulativeDistVal)){   
                //if(!Double.isNaN(addedVal)){                
                    for(int q=0; q<numOccurences; q++){
                        thisCoordCDF.add(cumulativeDistVal);
                        //thisCoordCDF.add(addedVal);
                    }
                }

                //thisCoordCDF.add(cumulativeDistVal);

                // - 1 because j++ each iteration
                j += (numOccurences - 1);
            

            }

            cumulativeDistribution.add(thisCoordCDF);
        }

        System.out.println("ALsOfCoords sorted: " + ALsOfCoords);
        System.out.println("cumulativeDistribution: " + cumulativeDistribution);


        return cumulativeDistribution;

    }

    private boolean isNonCritical( ArrayList<Double> andersonDarlingResult){
        boolean isNonCritical = true;

        //based on the research paper's suggestion. maybe modify based on other sources.
        double criticalValue = 1.8692;

        for(int i=0; i<andersonDarlingResult.size(); i++){
            if(andersonDarlingResult.get(i) > criticalValue){
                isNonCritical = false;
                return isNonCritical;
            }
        }


        return isNonCritical;
    }

    public StandardParametersQuantitative getParametresReglesQuantitives(){
        return m_parametresReglesQuantitatives;
    }


    private KMeansClusterer kMeansClusterer;
    private ArrayList rules = new ArrayList();
    //default num k clusters? need this?
    //private int NUM_K_CLUSTERS = 3;
    private ArrayList<Centroid> listOfCentroids = new ArrayList<Centroid>();
    private Map<Centroid, ArrayList<AssociationRule>> centroidClusters = new HashMap<Centroid, ArrayList<AssociationRule>>();
    StandardParametersQuantitative m_parametresReglesQuantitatives = null;

}