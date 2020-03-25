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
        //check that HashMap is the best choice - be able to justify this design decision
        centroidClusters = new HashMap<Centroid, ArrayList<AssociationRule>>();
        roundedCentroids = new ArrayList();

        m_parametresReglesQuantitatives = input_parametresReglesQuantitatives;

        for(Object a : assocRules) {
            kMeansAssocRules.add(a);
        }

    }

    /*public double testRuleProximities(){

        double avgOfAvgDists = 0;

        for(int i=0; i<kMeansAssocRules.size(); i++){
            AssociationRule ruleConsidered1 = (AssociationRule)kMeansAssocRules.get(i);

            ArrayList listOfIntervalsA = getQuantIntervals(ruleConsidered1);

            for(int j=0; j<kMeansAssocRules.size(); j++){
                if(i != j){

                    AssociationRule ruleConsidered2 = (AssociationRule)kMeansAssocRules.get(j);
                    ArrayList listOfIntervalsB = getQuantIntervals(ruleConsidered2);

                    double avgDist = 0;

                    //list of intervals has intervals ([, ], [, ], [, ]) etc in assocation rule from left to right
                    for(int a = 0; a < listOfIntervalsA.size(); a++){
                        float[] intervalA = (float[])listOfIntervalsA.get(a);
                        float[] intervalB = (float[])listOfIntervalsB.get(a);

                        float floatDistance = 0;
                        for(int b = 0; b<intervalB.length; b++){

                            float coordinate = Math.abs(intervalA[b] - intervalB[b]);
                            float coordSquared = coordinate*coordinate;
                            floatDistance += coordSquared;
                        
                        }
                        
                        double distance = Math.sqrt(floatDistance);

                        //add up all this distances
                        avgDist += distance;
                    }
                    
                    //avgDist /= listOfIntervalsA.size();
                    //-1 because don't find dist with self
                    avgDist /= (kMeansAssocRules.size() - 1);

                    avgOfAvgDists += avgDist;

                }
            }

        }

        avgOfAvgDists /= kMeansAssocRules.size(); 

        System.out.println(avgOfAvgDists);

        return avgOfAvgDists;

    }*/

    public double testRuleProximities(){

        double avgDistance = 0.0;

        int sizeOfARList = kMeansAssocRules.size();

        for(int i=0; i<sizeOfARList; i++){
            AssociationRule ruleConsidered1 = (AssociationRule)kMeansAssocRules.get(i);

            ArrayList listOfIntervalsA = getQuantIntervals(ruleConsidered1);

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

        //sum of nums from 1 to n is 0.5(n)(n-1)
        avgDistance /= ((sizeOfARList)*(sizeOfARList - 1) * 0.5);

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


    /*public ArrayList applyGMeansAlgo(){
        
        ArrayList gMeansCentroids = applyKMeansAlgo(1);

    }*/

    public ArrayList applyKMeansAlgo(){
        //run k means with default number of k clusters
        return applyKMeansAlgo(NUM_K_CLUSTERS);
    }

    public ArrayList applyKMeansAlgo(int numForK){
    /*    return applyKMeansAlgo(numForK, null);
    }

    //parametrized k means algo, so that we can call this repeatedly in g means
    public ArrayList applyKMeansAlgo(int numForK, ArrayList<Centroid> centroidsInput){*/

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

            //left part of rule:
            
            for (iIndiceItem = 0; iIndiceItem < ruleConsidered.m_iNombreItemsGauche; iIndiceItem++) {

                
                
                item = ruleConsidered.ObtenirItemGauche(iIndiceItem);
                
                if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {

                    itemQuant = (ItemQuantitative)item;  

                    //LHS interval min and max for quantitative LHS
                    //System.out.println("LHS itemQuant IntervalleMin: " + itemQuant.m_tBornes[0]);
                    //System.out.println("LHS itemQuant IntervalleMax: " + itemQuant.m_tBornes[1]);

                    //set min of left interval, if smaller

                    //idea: if that interval's min is greater, or max is lesser, replace

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
                
            //right part of rule: 

            for (iIndiceItem=0;iIndiceItem<ruleConsidered.m_iNombreItemsDroite;iIndiceItem++) {

            
                
                item = ruleConsidered.ObtenirItemDroite(iIndiceItem);
                
                if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {

                    itemQuant = (ItemQuantitative)item;  

                    //LHS interval min and max for quantitative LHS
                    //System.out.println("RHS itemQuant IntervalleMin: " + itemQuant.m_tBornes[0]);
                    //System.out.println("RHS itemQuant IntervalleMax: " + itemQuant.m_tBornes[1]);

                    //set min of left interval, if smaller

                    //idea: if that interval's min is greater, or max is lesser, replace

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

        //for(int i=0; i< NUM_K_CLUSTERS; i++){

        //if(centroidsInput == null || centroidsInput.size() == 0){
            for(int i=0; i< numForK; i++){
                listOfCentroids.add(generateRandomCentroid(intervals));
            }
      /*  }else{
            if(numForK == centroidsInput.size()){
                for(int i=0; i<centroidsInput.size(); i++){
                    listOfCentroids.add(centroidsInput.get(i));
                }
            }else{
                // THROW THIS AS AN ERROR IN THE FUTURE! OR ADD RANDOM CENTROIDS? IDK.
                System.out.println("ERROR: numForK != centroidsInput.size()");
            }

        }*/
 


        ArrayList listOfCentroidsPrev = new ArrayList();
        ArrayList roundedCentroids = new ArrayList();

        //for(int s=0; s<NUM_GENERATIONS; s++){
           //you need to round so that this doesn't potentially go on for a very long / indefinite amount of time
        //s ensures this is not an infinite loop
        int numIterations = 0;
        while(numIterations < NUM_GENERATIONS && (!equalLists(roundedCentroids, listOfCentroidsPrev) || roundedCentroids.size() == 0 && listOfCentroidsPrev.size() == 0)){
            //try to make into 1 for loop for more efficiency? but also before and after generate random centroid... so I'm not sure if it's possible... but check.
            //find nearest centroid for each rule

            //clear centroidClusters
            centroidClusters.clear();

            for(int i=0; i<kMeansAssocRules.size(); i++){

                //maybe initialize outside of the for loop, b/c that would be less expensive.
                AssociationRule ruleConsidered = (AssociationRule)kMeansAssocRules.get(i);

                ArrayList<ItemQuantitative> itemsQuant = new ArrayList();

                itemsQuant = findItemsQuant(ruleConsidered);
                
                Centroid nearestCentroid = findNearestCentroid(itemsQuant);

                if(!centroidClusters.containsKey(nearestCentroid)){
                    ArrayList<AssociationRule> rulesForCentroid = new ArrayList<AssociationRule>();
                    centroidClusters.put(nearestCentroid, rulesForCentroid);
                }
                
                //centroidClusters.get(nearestCentroid) is the ArrayList with the rules for the centroid's cluster
                (centroidClusters.get(nearestCentroid)).add(ruleConsidered);

            }

            //get rounded centroid coordinates (for comparison, to see when to stop iterating in the k-means algorithm)

            listOfCentroidsPrev = (ArrayList)listOfCentroids.clone();
            listOfCentroidsPrev = roundCentroids(listOfCentroidsPrev);

            //relocate centroids
            ArrayList newCentroids = relocateCentroids(centroidClusters);
            //System.out.println("New centroids: " + newCentroids);
            roundedCentroids = roundCentroids(newCentroids);

            //numIterations ensures that the loop does not run forever. only consider if lists not equal if we have not exceeded the max number of iterations
            if(numIterations < NUM_GENERATIONS - 1 && !equalLists(roundedCentroids, listOfCentroidsPrev)){
            //if(s < NUM_GENERATIONS - 1){
               listOfCentroids = newCentroids;

                //reset centroid clusters
                // if(s!=NUM_GENERATIONS - 1){
                //centroidClusters = new HashMap<Centroid, ArrayList>();
            }

            System.out.println("listOfCentroidsPrev: " + listOfCentroidsPrev);
            System.out.println("new - roundedCentroids: " + roundedCentroids);
            System.out.println("listOfCentroids: " + listOfCentroids);

            numIterations++;
            
        } // end of while loop

        int totalNumPoints = 0;
        for(int i=0; i<listOfCentroids.size(); i++){
            //null pointer error in centroidCluster.get??
            if(centroidClusters.get(listOfCentroids.get(i)) != null){
                totalNumPoints += centroidClusters.get(listOfCentroids.get(i)).size();
            }

            System.out.println("totalNumPts: " + totalNumPoints);
        }

        System.out.println("FINAL ASSOCIATION RULES: ");

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
        
        System.out.println("ruleProximity: " + testRuleProximities());

        double ruleProximity = testRuleProximities();
        System.out.println("(double)(ruleProximity / 50): " + (double)(ruleProximity / 50));

        for(int i=0; i<listOfCentroids.size(); i++){

            Centroid centroidConsidered = listOfCentroids.get(i);

            //if(centroidClusters.get(centroidConsidered) == null || centroidClusters.get(centroidConsidered).size() == 0){
                
                //remove from centroidClusters too?
                //centroidClusters.remove(centroidConsidered);

                //listOfCentroids.remove(i);
                //i--;
            //}

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

                double distanceBtwnCentroids = calculateEuclideanDistance(centroidConsidered.getCoordinates(), centroidInComparison.getCoordinates());
                if(j != i){
                    System.out.println(distanceBtwnCentroids);

                    //use euclidean distance to remove 'similar' rules. need to base on average euclidean dist? IOW, how to find how far to consider 'distinct'?
                    if(distanceBtwnCentroids < (double)(ruleProximity / 50)){
                        //question: remove centroids with numOcc == 0 first?
                        centroidConsidered.setNumOccurrences(centroidConsidered.getNumOccurrences() + centroidInComparison.getNumOccurrences());

                        centroidConsidered.setSupport(centroidConsidered.getSupport() + centroidInComparison.getSupport());

                        centroidConsidered.setConfidence(centroidConsidered.getConfidence() + centroidInComparison.getConfidence());

                        listOfCentroids.remove(j);
                        j--;
                    }
                    //continue;
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

        System.out.println("in constructCentroidRule");
        String centroidRule = "";
        
        if(centroidClusters.get(thisCentroid) == null){
            return centroidRule;
        }

        //int numOccurrences = centroidClusters.get(thisCentroid).size();
        int numOccurrences = thisCentroid.getNumOccurrences();

        System.out.println(totalNumPoints);

        /*int support = 0;
        int confidence = 0;

        if(totalNumPoints > 0){
            support = ((100*numOccurrences)/totalNumPoints);
        }

        if(support > 0){
            confidence = ((100*numOccurrences)/support);
        }*/

        int support = thisCentroid.getSupport();
        int confidence = thisCentroid.getConfidence();
        
        centroidRule += ("support = " + numOccurrences + " (" + support + "%) , ");

        centroidRule += ("confidence = " + confidence + " %  :  ");
        
        AssociationRule firstRuleMapped = (AssociationRule)centroidClusters.get(thisCentroid).get(0); // to get the items on left and right, since same for all mapped rules for that Centroid
        int r = 0;
        for(int j=0; j<firstRuleMapped.m_iNombreItemsGauche; j++){
            //and, or, etc? how to tell this
            Item item = firstRuleMapped.ObtenirItemGauche(j);

            if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {
                
                //just get 0th item's to string, since we want the labels, not the quantitative values, but we will just use the labels frm the 0th item
                String stringToPrint = ((ItemQuantitative)item).toString(0);
                if(stringToPrint.contains("[")){
                    stringToPrint = stringToPrint.substring(0, stringToPrint.indexOf("[") - 1);
                }
                System.out.print(stringToPrint);
                centroidRule += stringToPrint;
            
                System.out.print(" [ " + thisCentroid.getRoundedCoordinates().get(r) + " , " + thisCentroid.getRoundedCoordinates().get(r+1) + " ]");
                centroidRule += (" [ " + thisCentroid.getRoundedCoordinates().get(r) + " , " + thisCentroid.getRoundedCoordinates().get(r+1) + " ]");
                r = r+2;
            }else{
                System.out.print(((ItemQualitative)item).toString());
                centroidRule += (((ItemQualitative)item).toString());
            }
            
            if(j < firstRuleMapped.m_iNombreItemsGauche - 1){
                //Question: is it always AND, or is it ever OR? if so, when?
                System.out.print(" AND ");
                centroidRule += " AND ";
            }
        }

        System.out.print("  -->  ");
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
                System.out.print(stringToPrint);
                centroidRule += stringToPrint;
                System.out.print(" [ " + thisCentroid.getRoundedCoordinates().get(r) + " , " + thisCentroid.getRoundedCoordinates().get(r+1) + " ]");
                centroidRule += (" [ " + thisCentroid.getRoundedCoordinates().get(r) + " , " + thisCentroid.getRoundedCoordinates().get(r+1) + " ]");
                r = r+2;
            }else{
                System.out.print(((ItemQualitative)item).toString());
                centroidRule += (((ItemQualitative)item).toString());
            }
            if(j < firstRuleMapped.m_iNombreItemsDroite - 1){
                //Question: is it always AND, or is it ever OR? if so, when?
                System.out.print(" AND ");
                centroidRule += " AND ";
            }
        }

        System.out.println(" ");

        return centroidRule;
        
    }

    //optimize this... intensive operation
    private boolean equalLists(ArrayList list1, ArrayList list2){
        
        if(list1.size() == list2.size()){
            for(int i=0; i<list1.size(); i++){

                //.contains uses .equals(), so good.
                //issue: object to centroid conversion issues?
               /* if(!list2.contains((Centroid)list1.get(i))){
                    System.out.println("LISTS NOT EQUAL");
                    return false;
                }*/

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
                    System.out.println("LISTS NOT EQUAL");
                    return false;
                }

            }
        }else{
            System.out.println("LISTS NOT EQUAL");
            return false;
        }

        System.out.println("LISTS EQUAL");
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

    //issue with calcEuclDist
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

        System.out.println("randomCentroid: " + randomCentroid);

        return randomCentroid;
        
    }

    private float[] initialInterval = new float[]{Float.MAX_VALUE, -Float.MAX_VALUE};  

    public Centroid findNearestCentroid(ArrayList<ItemQuantitative> itemsQuant){

        //coordinates of the centroid to test against the random centroids
        ArrayList myCoordinates = new ArrayList();

        myCoordinates = getCoordinatesForItemsQuant(itemsQuant);
        
        double minDistance = Double.MAX_VALUE;

        //issue: what if first centroid, with 0, is the closest one?
        Centroid nearestCentroid = new Centroid();

        for(int i=0; i<listOfCentroids.size(); i++){
            //IF DISTANCE BETWEEN IS LESS THAN MINDISTANCE REPLACE MINDISTANCE
            //RETURN CLOSEST

            //currCentroid is the current random centroid
            Centroid currCentroid = (Centroid)(listOfCentroids.get(i));

            //find distance between randmo current centroid and myCoordinates, the coordinates of the centroid considered
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

    //hard-coded for now
    //we need to use g-means, learn num_k_clusterss
    private int NUM_K_CLUSTERS = 3;
    private int NUM_GENERATIONS = 1000;
    private ArrayList kMeansAssocRules; 
    private ArrayList<Centroid> listOfCentroids = new ArrayList<Centroid>();
    private Map<Centroid, ArrayList<AssociationRule>> centroidClusters = new HashMap<Centroid, ArrayList<AssociationRule>>();
    //list of rounded centroids, given a list of centroids
    private ArrayList roundedCentroids = new ArrayList();
    StandardParametersQuantitative m_parametresReglesQuantitatives = null;



}