package src.solver;

import java.util.*;
import src.apriori.*;
import src.database.*;
import src.geneticAlgorithm.*;

/* GMeansClusterer is still a work in progress functionality. Therefore, it will have some commented out print statements, and will be 
revised in the future. */
public class GMeansClusterer {

    //parametrized constructor
    public GMeansClusterer(ArrayList assocRules, StandardParametersQuantitative input_parametresReglesQuantitatives){

        TesterClustering.printOutRules(assocRules);
        TesterClustering.printOutLHVals(assocRules);
        TesterClustering.printOutRHVals(assocRules);

        rules = assocRules;

        m_parametresReglesQuantitatives = input_parametresReglesQuantitatives;

        kMeansClusterer = new KMeansClusterer(assocRules, m_parametresReglesQuantitatives);

        listOfCentroids = new ArrayList<Centroid>();
        centroidClusters = new HashMap<Centroid, ArrayList<AssociationRule>>();

    }
    
    public ArrayList applyGMeansAlgo(){
        return new ArrayList();
    }

    public ArrayList applyGMeansAlgo(int maxKClusterAmt){
        
        int i=0;
        int totalNumKClusters = 0;

        listOfCentroids = kMeansClusterer.applyKMeansAlgo(1);

        while(i < listOfCentroids.size() && totalNumKClusters < maxKClusterAmt){

           // System.out.println("G MEANS LIST OF CENTROIDS SIZE: " + listOfCentroids.size());

           // System.out.println("current centroid G MEANS: " + listOfCentroids.get(i));

            //centroidClusters will be the entirety of the data set, since all points map to the one cluster
            centroidClusters = kMeansClusterer.getCentroidClusters();

            //Uncomment to verify centroidClusters has all the points (since one key, and all points as values for the key)
            //System.out.println(centroidClusters.size());
            Centroid oldCentroid = listOfCentroids.get(i);
            ArrayList<AssociationRule> initialDataPoints = centroidClusters.get(oldCentroid);

            System.out.println("initialDataPoints: " + initialDataPoints);

            if(initialDataPoints == null){
                i++;
                continue;
            }

            // For when other times, when data points restricted:
            KMeansClusterer newKMeansClusterer = new KMeansClusterer(initialDataPoints, m_parametresReglesQuantitatives);

            // Get 2 clusters for data points
            //ArrayList<Centroid> newListOfCentroids = newKMeansClusterer.applyKMeansAlgo(numKClusters + 1, listOfCentroids);
            ArrayList<Centroid> newListOfCentroids = newKMeansClusterer.applyKMeansAlgo(2);

            Map<Centroid, ArrayList<AssociationRule>> newCentroidClusters = newKMeansClusterer.getCentroidClusters();

            // Uncomment for value checking
            // System.out.println("newCentroidClusters.keySet(): " + newCentroidClusters.keySet());

            // System.out.println("newCentroidClusters.keySet().size(): " + newCentroidClusters.keySet().size());

            // System.out.println("newListOfCentroids.size(): " + newListOfCentroids.size());

            Centroid c1 = new Centroid();
            Centroid c2 = new Centroid();;

            if(newListOfCentroids.size() == 2){

                c1 = newListOfCentroids.get(0);
                c2 = newListOfCentroids.get(1);
            

                if(c1 != null && c2 != null){
                    // Vector will be <c1 coordinates> + t<direction vector>. Represent as ArrayList: {c1 coordinates, direction vector}
                    ArrayList<ArrayList> vectorAL = createVector(c1, c2);

                    //project points in initialDataPoints onto vectorAL

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
                        Centroid removedCentroid = listOfCentroids.get(i);
                        //added a cluster

                        if((removedCentroid.getCoordinates()).equals(c2.getCoordinates()) || (removedCentroid.getCoordinates()).equals(c2.getCoordinates())){
                            i++;
                        }else{

                            //only add if one of the new centroids does not equal the old one, since otherwise an infinitely long recursive loop occurs.
                            listOfCentroids.remove(i);
                            listOfCentroids.add(i, c2);
                            listOfCentroids.add(i, c1);

                            //i stays the same
                            centroidClusters.remove(oldCentroid);
                            centroidClusters.put(c1, newCentroidClusters.get(c1));
                            centroidClusters.put(c2, newCentroidClusters.get(c2));
                            totalNumKClusters++;
                        }
                        
                        
                    }

                }

            }else{
                //throw error
                System.out.println("ERROR: newListOfCentroids.size() != 2");
                //IMPORTANT: 
                //note: this can happen if the new list of centroids removes a centroid that had no points mapped to it. What do I do here? keep the one centroid??
                i++;
            }
    
        }


        listOfCentroids = cleanUpRepeatRules(listOfCentroids);

        return listOfCentroids;

    }

    private ArrayList<Centroid> cleanUpRepeatRules(ArrayList<Centroid> listOfCentroids){

        ArrayList<Centroid> cleanedUpList = listOfCentroids;

        double ruleProximity = testRuleProximities(listOfCentroids);

        for(int i=0; i<listOfCentroids.size(); i++){

            Centroid centroidConsidered = listOfCentroids.get(i);

            // Uncomment to get minimum support and minimum confidence values
           //  System.out.println("m_parametresReglesQuantitatives.m_fMinSupp: " + m_parametresReglesQuantitatives.m_fMinSupp);
            // System.out.println("m_parametresReglesQuantitatives.m_fMinConf: " + m_parametresReglesQuantitatives.m_fMinConf);

            // TODO: only display values above a certain support or confidence threshold value
           /* if(centroidConsidered.getSupport() < m_parametresReglesQuantitatives.m_fMinSupp || centroidConsidered.getConfidence() < m_parametresReglesQuantitatives.m_fMinConf ){
                listOfCentroids.remove(i);
                continue;
            }*/

            for(int j=0; j<listOfCentroids.size(); j++){

                Centroid centroidInComparison = listOfCentroids.get(j);

                double distanceBtwnCentroids = kMeansClusterer.calculateEuclideanDistance(centroidConsidered.getCoordinates(), centroidInComparison.getCoordinates());
                if(j != i){
                    System.out.println(distanceBtwnCentroids);

                    // Use euclidean distance to remove 'similar' rules. TODO: how to find how far to consider 'distinct'?
                    if(distanceBtwnCentroids < (double)(ruleProximity)){

                        centroidConsidered.setNumOccurrences(centroidConsidered.getNumOccurrences() + centroidInComparison.getNumOccurrences());

                        centroidConsidered.setSupport(centroidConsidered.getSupport() + centroidInComparison.getSupport());

                        centroidConsidered.setConfidence(centroidConsidered.getConfidence() + centroidInComparison.getConfidence());

                        cleanedUpList.remove(j);

                        j--;
                    }
                }

            }

        }

        return cleanedUpList;

    }
        
    // [Tester function] Return the average distance between all Centroids in centroidList (passed in as a parameter)
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

                // List of intervals: has intervals ([, ], [, ], [, ]) etc in assocation rule from left to right
                for(int a = 0; a < listOfIntervalsA.size(); a++){
                    
                    //TODO: need CHECKING for if list of intervals' sizes not the same
                    //checks if double or float
                    double coordinate = (double)Math.abs((float)listOfIntervalsA.get(a) - (float)listOfIntervalsB.get(a));

                    double coordSquared = coordinate*coordinate;
                    doubleDistance += coordSquared;
                
                }

                double distance = Math.sqrt(doubleDistance);
                avgDist += distance;


            }

        }

        //sum of nums from 1 to (n - 1) is 0.5(n-1)(n-2)
        //check that this is not negative! if sizeOfARList is small.
        avgDist /= ((centListSize - 1)*(centListSize - 2) * 0.5);

        return avgDist;

    }

    // AL representation of vector returned will be <c1 coordinates> + t<direction vector>. Represent as ArrayList: {c1 coordinates, direction vector}
    private ArrayList<ArrayList> createVector(Centroid c1, Centroid c2){

        ArrayList vector = new ArrayList();

        ArrayList c1Coords = c1.getCoordinates();
        ArrayList c2Coords = c2.getCoordinates();

        vector.add(c1Coords);

        ArrayList<Float> directionVector = new ArrayList<Float>();

        if(c1Coords.size() == c2Coords.size()){
            for(int i=0; i<c2Coords.size(); i++){
                float difference = (Float)c2Coords.get(i) - (Float)c1Coords.get(i);
                directionVector.add(difference);
            }

            //System.out.println("directionVector: " + directionVector);

        }else{
            System.out.println("ERROR: c1Coords.size() != c2Coords.size()");
        }

        vector.add(directionVector);
    

        return vector;

    }

    private ArrayList<Centroid> projectPoints(ArrayList<AssociationRule> dataPoints, ArrayList<ArrayList> vectorAL){

        // AL of centroids, to be the projected points onto the vector
        ArrayList<Centroid> projectedPoints = new ArrayList<Centroid>();

        //vectorAL.get(0) is c1. vectorAL.get(1) is direction vector
        for(int i=0; i<dataPoints.size(); i++){
            AssociationRule rule = dataPoints.get(i);

            // Each elt of quantIntervalsForRule is a tuple {lower bd, upper bd}
            ArrayList<float[]> quantIntervalsForRule = kMeansClusterer.getQuantIntervals(rule);
            ArrayList coordinatesForRule = new ArrayList();
            for(int j=0; j<quantIntervalsForRule.size(); j++){
                coordinatesForRule.add(quantIntervalsForRule.get(j)[0]);
                coordinatesForRule.add(quantIntervalsForRule.get(j)[1]);
            }

            //Make a centroid for the rule, with the coordinates being coordinatesForRule
            Centroid centroidForRule = new Centroid(coordinatesForRule);

            //seems inefficient to make new centroid out of c1 coords... find a better way

            ArrayList c1Coords = (ArrayList)(vectorAL.get(0));
            Centroid c1copy = new Centroid(c1Coords);

            ArrayList vectorToRulePt = createVector(c1copy, centroidForRule);

            // Distance: dot product of vectorAL and vectorToRulePt, over magnitude of vector AL squared
            float distance = 0;

            float dotProduct = 0;

            ArrayList v1Direction = (ArrayList)(vectorAL.get(1));
            ArrayList v2Direction = (ArrayList)(vectorToRulePt.get(1));

            // TODO: resolve issue of NaN if init to 0, but otherwise get NaN error...
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

                // projectedPoints is made into an AL of centroids
                projectedPoints.add(projectedCentroid);
            }


        }

        // Uncomment to check projectedPoints value
        // System.out.println("projectedPoints: " + projectedPoints);

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

            ArrayList<Float> coords = currCentroid.getCoordinates();

            ArrayList<Float> standardizedCoords = new ArrayList<Float>();

            for(int j=0; j<coords.size(); j++){
                //TODO fix issue: stdDev = 0; divide by 0 --> get float NaN issue
                float stdDevCoord = (float)stdDev.get(j).floatValue();

                if(stdDevCoord == 0){
                    //stdDevCoord == 0 means all the points are the same for that coordinate val
                    standardizedCoords.add((float)(coords.get(j) - (double)mean.get(j)));
                }else{
                    standardizedCoords.add((float)(coords.get(j) - mean.get(j)) / stdDevCoord);
                }

            }

            Centroid standardizedCent = new Centroid(standardizedCoords);
            standardizedPoints.add(standardizedCent);

        }

        ArrayList<Float> meanStandard = getMean(standardizedPoints);

        ArrayList<Double> stdDevStandard = getStdDev(meanStandard, standardizedPoints);

        // Uncomment to test values
       //  System.out.println("meanStandard: " + meanStandard + " | stdDevStandard: " + stdDevStandard);

       //  System.out.println("standardizedPoints: " + standardizedPoints);

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
            ArrayList<Float> coords = currCentroid.getCoordinates();
            for(int j=0; j<coords.size(); j++){

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
            ArrayList<Float> coords = currCentroid.getCoordinates();
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

    // Anderson Darling test (a statistical test of whether a given sample of data is drawn from a given probability distribution) for normal distribution
    private  ArrayList<Double> andersonDarlingTest(ArrayList<Centroid> standardizedPoints){
        //double andersonDarlingResult = 0.0;

        int numPoints = standardizedPoints.size();

        ArrayList<ArrayList<Double>> cumulativeDistribution = applyCumulativeDistributionFxn(standardizedPoints);

        ArrayList<Double> aSqZ = new ArrayList<Double>();

        for(int i=0; i<cumulativeDistribution.size(); i++){
            ArrayList<Double> CDforCoord = cumulativeDistribution.get(i);
            int CDCoordsize = CDforCoord.size();
            for(int j=0; j<CDCoordsize; j++){
                
                //TODO fix issue: getting NaN when second log input is negative... and log only defined for x > 0
                double addedVal = ((2*(j + 1) - 1) * (Math.log(CDforCoord.get(j)) + Math.log(1 - CDforCoord.get(CDCoordsize - 1 - j))));

                //TODO fix issue: need to check for NaN -- but how to avoid with NaN not impacting calculations?
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

        // Uncomment to test aSqZ value 
        // System.out.println("aSqZ: " + aSqZ);

        ArrayList<Double> aSqZStar = new ArrayList<Double>();

        for(int i=0; i<aSqZ.size(); i++){
            ArrayList<Double> CDforCoord = cumulativeDistribution.get(i);
            int n = CDforCoord.size();

            aSqZStar.add(aSqZ.get(i) * (1 + 4/(n - 25/(Math.pow(n, 2)))));

        }

        // Uncomment to test aSqZStar value (the metric for the A-D test result)
        //System.out.println("aSqZStar: " + aSqZStar);

        return aSqZStar;
    }

    private ArrayList<ArrayList<Double>> applyCumulativeDistributionFxn(ArrayList<Centroid> standardizedPoints){
        
        // Has the cumulative distribution of each x_i, for each x_i in the coordinates of the centroid
        ArrayList<ArrayList<Double>> cumulativeDistribution = new ArrayList<ArrayList<Double>>();

        // ArrayList with elts that are ALs of all the vals for each coordinate x_i
        ArrayList<ArrayList> ALsOfCoords = new ArrayList<ArrayList>();

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

        //ALsOfCooords has, at each index, all the coordinates for that coordinate place.
        for(int i=0; i<ALsOfCoords.size(); i++){

            ArrayList thisCoordAL = ALsOfCoords.get(i);

            // Make all values their absolute values, because we need the magnitude, and will be squared in computations
            for(int j=0; j<thisCoordAL.size(); j++){
               
                Object coord = thisCoordAL.get(j);
                
                if(coord instanceof Number){
                    thisCoordAL.set(j, Math.abs(((Number)coord).doubleValue()));
                }
               
            }

            Collections.sort(thisCoordAL);

            // Transform into a hashmap with key = x value, value = num. occurrences
            HashMap<Object, Integer> thisCoordHM = new HashMap<Object, Integer>();
            for(int j=0; j<thisCoordAL.size(); j++){
                Object thisCoord = thisCoordAL.get(j);
                if(thisCoordHM.containsKey(thisCoord)){
                    thisCoordHM.put(thisCoord, (int)thisCoordHM.get(thisCoord) + 1);
                    //TODO: remove double occurrence?

                }else{
                    thisCoordHM.put(thisCoord, 1);
                }
            }

            int numDistinctVals = thisCoordHM.keySet().size();

            ArrayList<Double> thisCoordCDF = new ArrayList<Double>();
            double cumulativeDistVal = 0.0;

            double prevVal = 0.0;

            //TODO fix issue: cumulative distribution should be between 0 and 1, and this is not the case.
            for(int j=0; j<thisCoordAL.size(); j++){

                //TODO: throw conversion error if applicable
                double coordDoubleVal = (double)thisCoordAL.get(j);

                int numOccurences = thisCoordHM.get(thisCoordAL.get(j));

                double power = (-0.5 * coordDoubleVal * coordDoubleVal);
                double ePower = Math.exp(power);
                double sqrt2Pi = Math.sqrt(2 * Math.PI);

                //for summation / Riemann sum, multiply height (f(x) val) times change in x.
                double fXVal = (ePower / sqrt2Pi);

                //change in x value
                double dx = 0.0;

                if(j == 0){
                    dx = coordDoubleVal;
                }else{
                   dx = coordDoubleVal - prevVal;
                }

                double addedVal = 0.0;

                //numDistinctVals is the 'number of rectangles' in the Riemann sum
                addedVal = (numOccurences * (fXVal * dx)) / numDistinctVals;

                // TODO: check that this relies on other values, and does so correctly
                cumulativeDistVal += addedVal;
                
                // TODO: need to make sure doesn't increment each time if numOccurrences > 1.
                if(!Double.isNaN(cumulativeDistVal)){                 
                    for(int q=0; q<numOccurences; q++){
                        thisCoordCDF.add(cumulativeDistVal);
                    }
                }

                //set prevVal, for next iteration
                prevVal = (double)thisCoordAL.get(j);
                // - 1 because j++ each iteration
                j += (numOccurences - 1);
            

            }

            cumulativeDistribution.add(thisCoordCDF);
        }

        // Uncomment to check the cumulative distribution
        // System.out.println("ALsOfCoords sorted: " + ALsOfCoords);
        // System.out.println("cumulativeDistribution: " + cumulativeDistribution);


        return cumulativeDistribution;

    }

    private boolean isNonCritical( ArrayList<Double> andersonDarlingResult){
        boolean isNonCritical = true;

        // Based on the research paper's suggestion. Maybe modify based on other sources.
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
    private ArrayList<Centroid> listOfCentroids = new ArrayList<Centroid>();
    private Map<Centroid, ArrayList<AssociationRule>> centroidClusters = new HashMap<Centroid, ArrayList<AssociationRule>>();
    StandardParametersQuantitative m_parametresReglesQuantitatives = null;

}