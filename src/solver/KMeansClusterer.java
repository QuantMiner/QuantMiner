package src.solver;

import java.util.*;
import src.apriori.*;
import src.database.*;
import src.geneticAlgorithm.*;

public class KMeansClusterer {

    //parametrized constructor
    public KMeansClusterer(ArrayList assocRules, StandardParametersQuantitative input_parametresReglesQuantitatives){

        kMeansAssocRules = new ArrayList();
        listOfCentroids = new ArrayList<Centroid>();
        centroidClusters = new HashMap<Centroid, ArrayList<AssociationRule>>();
        roundedCentroids = new ArrayList();

        m_parametresReglesQuantitatives = input_parametresReglesQuantitatives;

        for(Object a : assocRules) {
            kMeansAssocRules.add(a);
        }

    }

    // Return the average distance between all AssocationRules in kMeansAssocRules
    public double testRuleProximities(){

        double avgDistance = 0.0;

        int sizeOfARList = kMeansAssocRules.size();

        for(int i=0; i<sizeOfARList; i++){
            AssociationRule ruleConsidered1 = (AssociationRule)kMeansAssocRules.get(i);

            ArrayList listOfIntervalsA = getQuantIntervals(ruleConsidered1);

            int numDifferentPoints = 0;

            for(int j=i+1; j<sizeOfARList; j++){

                AssociationRule ruleConsidered2 = (AssociationRule)kMeansAssocRules.get(j);
                ArrayList listOfIntervalsB = getQuantIntervals(ruleConsidered2);

                float floatDistance = 0;
                //list of intervals has intervals ([, ], [, ], [, ]) etc in assocation rule from left to right
                for(int a = 0; a < listOfIntervalsA.size(); a++){
                    float[] intervalA = (float[])listOfIntervalsA.get(a);
                    float[] intervalB = (float[])listOfIntervalsB.get(a);

                    for(int b = 0; b<intervalB.length; b++){

                        float coordinate = Math.abs(intervalA[b] - intervalB[b]);
                        float coordSquared = coordinate*coordinate;

                        floatDistance += coordSquared;
                    
                    }
                    
                }

                double distance = Math.sqrt(floatDistance);

                //add up all this distances
                avgDistance += distance;
                
            }

        }

        //sum of nums from 1 to (n - 1) is 0.5(n-1)(n-2)
        avgDistance /= ((sizeOfARList-1)*(sizeOfARList - 2) * 0.5);

        return avgDistance;

    }

    public ArrayList<float []> getQuantIntervals(AssociationRule ruleConsidered){
        Item item = null;
        ItemQualitative itemQual = null;
        ItemQuantitative itemQuant = null;
        int iIndiceItem = 0;
        //each elt is an array of len 2: m_tBornes = {min, max}
        ArrayList listOfIntervals = new ArrayList();

        if (ruleConsidered == null){
            System.out.println("rule considered is null");
            return null;
        }

        for (iIndiceItem = 0; iIndiceItem < ruleConsidered.m_iNombreItemsGauche; iIndiceItem++) {

            item = ruleConsidered.ObtenirItemGauche(iIndiceItem);

            if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {

                itemQuant = (ItemQuantitative)item;  

                listOfIntervals.add(itemQuant.m_tBornes);

            }
        }


        for (iIndiceItem = 0; iIndiceItem < ruleConsidered.m_iNombreItemsDroite; iIndiceItem++) {

            item = ruleConsidered.ObtenirItemDroite(iIndiceItem);

            if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {

                itemQuant = (ItemQuantitative)item;  

                listOfIntervals.add(itemQuant.m_tBornes);

            }
        }

        return listOfIntervals;
    }


    private float[] originalInterval = new float[]{Float.MAX_VALUE, -Float.MAX_VALUE};

    public ArrayList applyKMeansAlgo(){
        //run k means with default number of k clusters
        return applyKMeansAlgo(NUM_K_CLUSTERS);
    }

    public ArrayList applyKMeansAlgo(int numForK){

        boolean leftIsQuantitative = false;
        boolean rightIsQuantitative = false;
        
        ArrayList<float[]> intervals = new ArrayList();
        
        //Get the min and max of intervals for quantitative LHS and/or RHS
        for(int i=0; i<kMeansAssocRules.size(); i++){
            
            //maybe initialize outside of the for loop, b/c that would be less expensive.
            AssociationRule ruleConsidered = (AssociationRule)kMeansAssocRules.get(i);

            Item item = null;
            ItemQualitative itemQual = null;
            ItemQuantitative itemQuant = null;
            int iIndiceItem = 0;
            int intervalIndexCount = 0;
            
            if (ruleConsidered == null){
                System.out.println("rule considered is null");
                return null;
            }

            //left part of rule, if quantitative:
            
            for (iIndiceItem = 0; iIndiceItem < ruleConsidered.m_iNombreItemsGauche; iIndiceItem++) {
                
                item = ruleConsidered.ObtenirItemGauche(iIndiceItem);
                
                if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {

                    itemQuant = (ItemQuantitative)item;  

                    float[] originalInterval = new float[]{Float.MAX_VALUE, -Float.MAX_VALUE};
                    if(intervals.size() <= intervalIndexCount){
                         intervals.add(originalInterval);
                    }

                    if(itemQuant.m_tBornes[0] <= intervals.get(intervalIndexCount)[0]){
                        intervals.get(intervalIndexCount)[0] = itemQuant.m_tBornes[0];
                    }

                    //set max of left interval, if smaller
                    if(itemQuant.m_tBornes[1] >= intervals.get(intervalIndexCount)[1]){
                         intervals.get(intervalIndexCount)[1] = itemQuant.m_tBornes[1];
                    }
                    
                    intervalIndexCount++;
                    
                }    
            }
                
            //right part of rule, if quantitative: 

            for (iIndiceItem=0;iIndiceItem<ruleConsidered.m_iNombreItemsDroite;iIndiceItem++) {

                item = ruleConsidered.ObtenirItemDroite(iIndiceItem);
                
                if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {

                    itemQuant = (ItemQuantitative)item;  


                    float[] originalInterval = new float[]{Float.MAX_VALUE, -Float.MAX_VALUE};
                    if(intervals.size() <= intervalIndexCount){
                            intervals.add(originalInterval);
                    }

                    if(itemQuant.m_tBornes[0] <= intervals.get(intervalIndexCount)[0]){
                        intervals.get(intervalIndexCount)[0] = itemQuant.m_tBornes[0];
                    }

                    //set max of left interval, if smaller
                    if(itemQuant.m_tBornes[1] >= intervals.get(intervalIndexCount)[1]){
                            intervals.get(intervalIndexCount)[1] = itemQuant.m_tBornes[1];
                    }
                    
                    intervalIndexCount++;
                    
                }    
            }    
        }

        //Generating random centroids

        for(int i=0; i< numForK; i++){
            listOfCentroids.add(generateRandomCentroid(intervals));
        }

        ArrayList listOfCentroidsPrev = new ArrayList();
        ArrayList roundedCentroids = new ArrayList();

        int numIterations = 0;
        while(numIterations < NUM_GENERATIONS && (!equalLists(roundedCentroids, listOfCentroidsPrev) || roundedCentroids.size() == 0 && listOfCentroidsPrev.size() == 0)){
            //find nearest centroid for each rule

            //clear centroidClusters
            centroidClusters.clear();

            for(int i=0; i<kMeansAssocRules.size(); i++){

                AssociationRule ruleConsidered = (AssociationRule)kMeansAssocRules.get(i);

                ArrayList<ItemQuantitative> itemsQuant = new ArrayList();

                itemsQuant = findItemsQuant(ruleConsidered);
                
                Centroid nearestCentroid = findNearestCentroid(itemsQuant);

                if(!centroidClusters.containsKey(nearestCentroid)){
                    ArrayList<AssociationRule> rulesForCentroid = new ArrayList<AssociationRule>();
                    centroidClusters.put(nearestCentroid, rulesForCentroid);
                }
                
                // centroidClusters.get(nearestCentroid) is the ArrayList with the rules for the centroid's cluster
                (centroidClusters.get(nearestCentroid)).add(ruleConsidered);

            }

            // get rounded centroid coordinates (for comparison, to see when to stop iterating in the k-means algorithm)

            listOfCentroidsPrev = (ArrayList)listOfCentroids.clone();
            listOfCentroidsPrev = roundCentroids(listOfCentroidsPrev);

            //r elocate centroids
            ArrayList newCentroids = relocateCentroids(centroidClusters);
            roundedCentroids = roundCentroids(newCentroids);

            // numIterations ensures that the loop does not run forever. only consider if lists not equal if we have not exceeded the max number of iterations
            if(numIterations < NUM_GENERATIONS - 1 && !equalLists(roundedCentroids, listOfCentroidsPrev)){
               listOfCentroids = newCentroids;
            }

            numIterations++;
            
        } // end of while loop

        int totalNumPoints = 0;
        for(int i=0; i<listOfCentroids.size(); i++){
            if(centroidClusters.get(listOfCentroids.get(i)) != null){
                totalNumPoints += centroidClusters.get(listOfCentroids.get(i)).size();
            }
        }

        for(int i=0; i<listOfCentroids.size(); i++){

            String centroidRule = "";

            Centroid thisCentroid = (Centroid)listOfCentroids.get(i);
            
            //set centroid's number of occcurrences
            if(centroidClusters.get(thisCentroid) == null){
                thisCentroid.setNumOccurrences(0);
            }else{
                thisCentroid.setNumOccurrences(centroidClusters.get(thisCentroid).size());
            }

            //set centroid's support
            if(totalNumPoints > 0){
                thisCentroid.setSupport(((100*thisCentroid.getNumOccurrences())/totalNumPoints));
            }else{
                thisCentroid.setSupport(0);
            }

            //set centroid's confidence

            int support = thisCentroid.getSupport();
           
            if(support > 0){
                thisCentroid.setConfidence(((100*thisCentroid.getNumOccurrences())/support));
            }else{
                thisCentroid.setConfidence(0);
            }

            centroidRule = constructCentroidRule(thisCentroid, totalNumPoints);

            thisCentroid.setCentroidRule(centroidRule);

        }
        
        // Rule proximity to use
        double ruleProximity = testRuleProximities();

        for(int i=0; i<listOfCentroids.size(); i++){

            Centroid centroidConsidered = listOfCentroids.get(i);

           // Uncomment to see the minimum support and confidence parameters
           // System.out.println("m_parametresReglesQuantitatives.m_fMinSupp: " + m_parametresReglesQuantitatives.m_fMinSupp);
           // System.out.println("m_parametresReglesQuantitatives.m_fMinConf: " + m_parametresReglesQuantitatives.m_fMinConf);

            for(int j=0; j<listOfCentroids.size(); j++){

                Centroid centroidInComparison = listOfCentroids.get(j);

                double distanceBtwnCentroids = calculateEuclideanDistance(centroidConsidered.getCoordinates(), centroidInComparison.getCoordinates());
                if(j != i){

                    // Use euclidean distance to remove 'similar' rules. TODO: consider how to find how far to consider 'distinct'
                    if(distanceBtwnCentroids < (double)(ruleProximity)){
                        centroidConsidered.setNumOccurrences(centroidConsidered.getNumOccurrences() + centroidInComparison.getNumOccurrences());

                        centroidConsidered.setSupport(centroidConsidered.getSupport() + centroidInComparison.getSupport());

                        centroidConsidered.setConfidence(centroidConsidered.getConfidence() + centroidInComparison.getConfidence());

                        listOfCentroids.remove(j);
                        j--;
                    }
                }
              

            }

        }

        //the rules are the list of centroids
        return listOfCentroids;
    }

    public Map<Centroid, ArrayList<AssociationRule>> getCentroidClusters(){

        return centroidClusters;

    }

    private String constructCentroidRule(Centroid thisCentroid, int totalNumPoints){

        String centroidRule = "";
        
        if(centroidClusters.get(thisCentroid) == null){
            return centroidRule;
        }

        int numOccurrences = thisCentroid.getNumOccurrences();

        int support = thisCentroid.getSupport();
        int confidence = thisCentroid.getConfidence();
        
        centroidRule += ("support = " + numOccurrences + " (" + support + "%) , ");

        centroidRule += ("confidence = " + confidence + " %  :  ");
        
        AssociationRule firstRuleMapped = (AssociationRule)centroidClusters.get(thisCentroid).get(0); // to get the items on left and right, since same for all mapped rules for that Centroid
        int r = 0;
        for(int j=0; j<firstRuleMapped.m_iNombreItemsGauche; j++){
            Item item = firstRuleMapped.ObtenirItemGauche(j);

            if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {
                
                //just get 0th item's to string, since we want the labels, not the quantitative values, but we will just use the labels frm the 0th item
                String stringToPrint = ((ItemQuantitative)item).toString(0);
                if(stringToPrint.contains("[")){
                    stringToPrint = stringToPrint.substring(0, stringToPrint.indexOf("[") - 1);
                }
                centroidRule += stringToPrint;
            
                centroidRule += (" [ " + thisCentroid.getRoundedCoordinates().get(r) + " , " + thisCentroid.getRoundedCoordinates().get(r+1) + " ]");
                r = r+2;
            }else{
                centroidRule += (((ItemQualitative)item).toString());
            }
            
            if(j < firstRuleMapped.m_iNombreItemsGauche - 1){
                centroidRule += " AND ";
            }
        }

        centroidRule += "  -->  ";

        for(int j=0; j<firstRuleMapped.m_iNombreItemsDroite; j++){

            //and, or, etc? how to tell this?
            Item item = firstRuleMapped.ObtenirItemDroite(j);
        
            if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {

                //just get 0th item's to string, since we want the labels, not the quantitative values, but we will just use the labels frm the 0th item
                String stringToPrint = ((ItemQuantitative)item).toString(0);
                if(stringToPrint.contains("[")){
                    stringToPrint = stringToPrint.substring(0, stringToPrint.indexOf("[") - 1);
                }
                centroidRule += stringToPrint;
                centroidRule += (" [ " + thisCentroid.getRoundedCoordinates().get(r) + " , " + thisCentroid.getRoundedCoordinates().get(r+1) + " ]");
                r = r+2;
            }else{
                centroidRule += (((ItemQualitative)item).toString());
            }
            if(j < firstRuleMapped.m_iNombreItemsDroite - 1){
                centroidRule += " AND ";
            }
        }

        return centroidRule;
        
    }

    //TODO: optimize this, since is intensive operation
    private boolean equalLists(ArrayList list1, ArrayList list2){
        
        if(list1.size() == list2.size()){
            for(int i=0; i<list1.size(); i++){

                boolean isInList = false;
                Centroid cent1 = (Centroid)list1.get(i);
                ArrayList<Double> coords1 = cent1.getCoordinates();

                for(int j=0; j<list2.size(); j++){
                    Centroid cent2 = (Centroid)list2.get(j);
                    ArrayList<Double> coords2 =  cent2.getCoordinates();
                    if(coords1.equals(coords2)){
                        isInList = true;
                    }
                }

                if(!isInList){
                    //Lists not equal
                    return false;
                }

            }
        }else{
            //Lists not equal
            return false;
        }

        //Lists equal
        return true;
    }


    public ArrayList roundCentroids(ArrayList listOfCentroids){
        roundedCentroids = new ArrayList();

        for(int i=0; i<listOfCentroids.size(); i++){

            Centroid roundedCent = new Centroid(((Centroid)listOfCentroids.get(i)).getRoundedCoordinates());
            roundedCentroids.add(roundedCent);
        
        }

        return roundedCentroids;
    }

    private ArrayList<ItemQuantitative> findItemsQuant(AssociationRule ruleConsidered){
        
        ArrayList<ItemQuantitative> itemsQuant = new ArrayList();

        for (int iIndiceItem = 0; iIndiceItem < ruleConsidered.m_iNombreItemsGauche; iIndiceItem++) {

            Item item = ruleConsidered.ObtenirItemGauche(iIndiceItem);

            if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {
                ItemQuantitative itemQuant = (ItemQuantitative)item;  
                itemsQuant.add(itemQuant);
            }
            
        }

        for (int iIndiceItem = 0; iIndiceItem < ruleConsidered.m_iNombreItemsDroite; iIndiceItem++) {

            Item item = ruleConsidered.ObtenirItemDroite(iIndiceItem);

            if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {
                ItemQuantitative itemQuant = (ItemQuantitative)item;  
                itemsQuant.add(itemQuant);
            }
            
        }

        return itemsQuant;

    }

    public double calculateEuclideanDistance(ArrayList coordinates1, ArrayList coordinates2){

        float sum = 0;
        for(int i=0; i<coordinates1.size(); i = i+2){
            float xCoordinate = Math.abs((float)coordinates1.get(i) - (float)coordinates2.get(i));
            float yCoordinate = Math.abs((float)coordinates1.get(i + 1) - (float)coordinates2.get(i + 1));

            float xCoordSquared = xCoordinate * xCoordinate;
            float yCoordSquared = yCoordinate * yCoordinate;
            sum = sum + xCoordSquared + yCoordSquared;
        
        }
        
        double distance = Math.sqrt(sum);

        return distance;

    }

    public Centroid generateRandomCentroid(ArrayList<float[]> intervals){

        Random r = new Random();

        //randomCoords[0] is random x coord, randomCoords[1] is random y coord, randomCoords[2] is random z coord, randomCoords[3] is random q coord, etc
        float[] randomCoords = new float[intervals.size() * 2];

        int k=0;

        //x and y random interval in i and i+1 index in randomCoords
        for(int i=0; i<intervals.size()*2; i = i+2){
            //random y coordinate 
            //intervals[i][0] is min of interval i for example
            randomCoords[i] = (intervals.get(k)[0] + r.nextFloat() * ((intervals.get(k))[1] - (intervals.get(k))[0]));
            //random y coordinate
            //intervals[i][1] is max of interval i for example
            //use random x coordinate as min because, since we are dealing with intervals as our "coordinates" for the Centroid, it only logically makes sense for randomYCoord to be >= randomXCoord 
            randomCoords[i+1] = randomCoords[i] + r.nextFloat() * ((intervals.get(k))[1] - randomCoords[i]);

            k++;

        }

        Centroid randomCentroid = new Centroid(randomCoords);

        return randomCentroid;
        
    }

    private float[] initialInterval = new float[]{Float.MAX_VALUE, -Float.MAX_VALUE};  

    public Centroid findNearestCentroid(ArrayList<ItemQuantitative> itemsQuant){

        //coordinates of the centroid to test against the random centroids
        ArrayList myCoordinates = new ArrayList();

        myCoordinates = getCoordinatesForItemsQuant(itemsQuant);
        
        double minDistance = Double.MAX_VALUE;

        Centroid nearestCentroid = new Centroid();

        for(int i=0; i<listOfCentroids.size(); i++){
 
            //currCentroid is the current random centroid
            Centroid currCentroid = (Centroid)(listOfCentroids.get(i));

            //find distance between random current centroid and myCoordinates, the coordinates of the centroid considered
            double euclidDistance = calculateEuclideanDistance(myCoordinates, currCentroid.getCoordinates());

            if(euclidDistance < minDistance){
                nearestCentroid = (Centroid)listOfCentroids.get(i);
                minDistance = euclidDistance;
            }

        }

        return (Centroid)nearestCentroid;
    }

    private ArrayList getCoordinatesForItemsQuant(ArrayList<ItemQuantitative> itemsQuant){

        ArrayList myCoordinates = new ArrayList();

        for(int i=0; i<itemsQuant.size(); i++){
            float minOfItem = itemsQuant.get(i).m_tBornes[0];
            float maxOfItem = itemsQuant.get(i).m_tBornes[1];

            myCoordinates.add(minOfItem);
            myCoordinates.add(maxOfItem);
        }

        return myCoordinates;

    }

    public ArrayList<Centroid> relocateCentroids(Map<Centroid, ArrayList<AssociationRule>> centroidClusters){
        ArrayList<Centroid> relocatedCentroids = new ArrayList<Centroid>();

        for(int i=0; i<listOfCentroids.size(); i++){
            Centroid thisCentroid = listOfCentroids.get(i);
            ArrayList mappedCoords = centroidClusters.get(thisCentroid);
            //centroidClusters.remove(thisCentroid);
            Centroid newCentroid = avgCentroidForCluster(thisCentroid, mappedCoords);

            relocatedCentroids.add(newCentroid);

        }

        return relocatedCentroids;

    }

    private Centroid avgCentroidForCluster(Centroid centroid, ArrayList mappedPoints){
        if(mappedPoints == null || mappedPoints.size() == 0){
           //Mapped coords is null
            return centroid;
        }

        ArrayList coordsAvg = new ArrayList();

        //each mapped points is an assocation rule
        for(int i=0; i<mappedPoints.size(); i++){
            ArrayList<ItemQuantitative> itemsQuant = new ArrayList();
            itemsQuant = findItemsQuant((AssociationRule)mappedPoints.get(i));
            ArrayList coordinatesForItems = getCoordinatesForItemsQuant(itemsQuant);
            for(int j=0; j<coordinatesForItems.size(); j++){
                if(coordsAvg.size() <= j){
                    coordsAvg.add((float)0);
                }
                
                coordsAvg.set(j, (float)coordsAvg.get(j) + (float)coordinatesForItems.get(j));


            }

        }

        for(int j=0; j<coordsAvg.size(); j++){
            //check that the size of coordsAvg and coordinatesForItems is the same?
            coordsAvg.set(j, (float)coordsAvg.get(j) / (float)mappedPoints.size());
        }


        Centroid avgCentroid = new Centroid(coordsAvg);
        return avgCentroid;


    }

    public StandardParametersQuantitative getParametresReglesQuantitives(){
        return m_parametresReglesQuantitatives;
    }

    // Hard-coded number of clusters for K-means. If want flexible number of clusters, use G-means.
    private int NUM_K_CLUSTERS = 3;
    private int NUM_GENERATIONS = 1000;
    private ArrayList kMeansAssocRules; 
    private ArrayList<Centroid> listOfCentroids = new ArrayList<Centroid>();
    private Map<Centroid, ArrayList<AssociationRule>> centroidClusters = new HashMap<Centroid, ArrayList<AssociationRule>>();
    //list of rounded centroids, given a list of centroids
    private ArrayList roundedCentroids = new ArrayList();
    StandardParametersQuantitative m_parametresReglesQuantitatives = null;



}