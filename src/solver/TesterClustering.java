package src.solver;

import java.util.*;
import src.apriori.*;
import src.database.*;
import src.geneticAlgorithm.*;

/* Tester class for the clutering methods */
public class TesterClustering {


    public static void printOutRules(ArrayList assocRules){
        for(int i=0; i<assocRules.size(); i++){
            
            // TODO: maybe initialize outside of the for loop, b/c that would be less expensive.
            AssociationRule ruleConsidered = (AssociationRule)assocRules.get(i);

            System.out.println(ruleConsidered.toString());

        }
    }

    //for a rule Quant [ _ , _] --> Qual
    public static void printOutLHVals(ArrayList assocRules){

        System.out.println("Lower bound vals: ");
        //Get the min and max of intervals for quantitative LHS and/or RHS
        for(int i=0; i<assocRules.size(); i++){
            
            // TODO: maybe initialize outside of the for loop, b/c that would be less expensive.
            AssociationRule ruleConsidered = (AssociationRule)assocRules.get(i);

            Item item = null;
            ItemQualitative itemQual = null;
            ItemQuantitative itemQuant = null;
            int iIndiceItem = 0;
            int intervalIndexCount = 0;
            
            if (ruleConsidered == null){
                System.out.println("rule considered is null");
            }

            //left part of rule:
            
            item = ruleConsidered.ObtenirItemDroite(iIndiceItem);
                
            if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {

                itemQuant = (ItemQuantitative)item;  

                System.out.println(itemQuant.m_tBornes[0]);
                
            }    
            
        }
        
    }

    //for a rule Quant [ _ , _] --> Qual
    public static void printOutRHVals(ArrayList assocRules){

        System.out.println("Upper bound vals: ");

        //Get the min and max of intervals for quantitative LHS and/or RHS
        for(int i=0; i<assocRules.size(); i++){
            
            //maybe initialize outside of the for loop, b/c that would be less expensive.
            AssociationRule ruleConsidered = (AssociationRule)assocRules.get(i);

            Item item = null;
            ItemQualitative itemQual = null;
            ItemQuantitative itemQuant = null;
            int iIndiceItem = 0;
            int intervalIndexCount = 0;
            
            if (ruleConsidered == null){
                System.out.println("rule considered is null");
            }

            //left part of rule:
            
            item = ruleConsidered.ObtenirItemDroite(iIndiceItem);
                
            if (item.m_iTypeItem == Item.ITEM_TYPE_QUANTITATIF) {

                itemQuant = (ItemQuantitative)item;  

                System.out.println(itemQuant.m_tBornes[1]);
                
            }    
            
        }
        
    }

}