package src.geneticAlgorithm;

import java.util.*;

public class Centroid {

        public Centroid(){
            coordinates = new ArrayList();
            roundedCoordinates = new ArrayList();
            hasBeenPrinted = false;

            initSuppConf();
        }

        public Centroid(float [] myCoordinates){
            
            coordinates = new ArrayList();
            roundedCoordinates = new ArrayList();
            hasBeenPrinted = false;
            for(int i=0; i<myCoordinates.length; i++){
                coordinates.add(myCoordinates[i]);
            }
            setRoundedCoordinates(coordinates);

            initSuppConf();


        }

        public Centroid(ArrayList myCoordinates){
            
            coordinates = new ArrayList();
            roundedCoordinates = new ArrayList();

            coordinates = myCoordinates;
            setRoundedCoordinates(coordinates);

            hasBeenPrinted = false;

            initSuppConf();

        }

        public void initSuppConf(){
            numOccurrences = 0;
            support = 0;
            confidence = 0;
        }

        public ArrayList getCoordinates(){
            return coordinates;
        }

        public void setRoundedCoordinates(ArrayList myCoordinates){

            for(int i=0; i<myCoordinates.size(); i++){
                Object coordinate = myCoordinates.get(i);
                if(coordinate instanceof Float){
                    double numToAdd = (Math.round((float)myCoordinates.get(i) * 10)) / 10.0;
                    roundedCoordinates.add(numToAdd);
                } else if(coordinate instanceof Double){
                    double numToAdd = (Math.round((double)myCoordinates.get(i) * 10)) / 10.0;
                    roundedCoordinates.add(numToAdd);
                }
                
            }

        }

        public ArrayList getRoundedCoordinates(){
            return roundedCoordinates;
        }

        

        public void setCoordinates(float[] newCoordinates){
            //or, if len 1, set x?? or not well-defined?
            if(newCoordinates.length >= coordinates.size()){
                for(int i=0; i<newCoordinates.length; i++){
                    coordinates.set(i, coordinates);
                }
            }

            setRoundedCoordinates(coordinates);

        }

        public String toString(){

            String returnString = "Centroid cordinates: [";

            for(int i=0; i<coordinates.size(); i++){
                returnString += coordinates.get(i);
                if(i != coordinates.size() - 1){
                    returnString += ", ";
                }
            }

            returnString += "]";

            return returnString;
        }

        public void setCentroidRule(String newCentroidRule){
            centroidRule = newCentroidRule;
        }

        public String getCentroidRule(){
            return centroidRule;
        }

        public void setHasBeenPrinted(boolean hasBeen){
            hasBeenPrinted = hasBeen;
        }

        public boolean getHasBeenPrinted(){
            return hasBeenPrinted;
        }

        public void setNumOccurrences(int amount){
            numOccurrences = amount;
        }

        public void setSupport(int mySupport){
            support = mySupport;
        }

        public void setConfidence(int myConfidence){
            confidence = myConfidence;
        }

        public int getNumOccurrences(){
            return numOccurrences;
        }

        public int getSupport(){
            return support;
        }

        public int getConfidence(){
            return confidence;
        }

        //coordinates have elements = each i, i+1 pair (i even) is min and max of interval (i.e. x, y coordinate pair)
        //coordinates has float values, for precision for Euclidean Algorithm calculations
        private ArrayList coordinates = new ArrayList();
        //roundedCoordinates has double values, for rounded for printing out - rounded coordinates for a centroid
        private ArrayList roundedCoordinates = new ArrayList();
        private String centroidRule = "";
        private boolean hasBeenPrinted = false;

        private int numOccurrences;
        private int support;
        private int confidence;
    


    }