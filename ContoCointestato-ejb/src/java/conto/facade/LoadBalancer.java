package conto.facade;

import java.util.Random;

import javax.ejb.LocalBean;
import javax.ejb.Stateful;

/**
 *
 * @author Damiano Di Stefano, Marco Giuseppe Salafia
 */
@Stateful
@LocalBean
public class LoadBalancer 
{    
    public String getCoordinator()
    {
        Random rand = new Random();
        
        int randomNum = rand.nextInt(2) + 1;
                
        switch (randomNum)
        {
            case 1:
                return "COO1";
            case 2:   
                return "COO2";
        }
        return null;
    }
}
