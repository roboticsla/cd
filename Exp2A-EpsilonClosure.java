import java.util.*;

class test
{
    public static void main(String args[])
    {
        int noofstates;
        Scanner s = new Scanner(System.in);
        System.out.println("Emter Number of States: ");
        noofstates = s.nextInt();
        
        ArrayList<Integer> states  = new ArrayList<Integer>();
        ArrayList<ArrayList<Integer>> transitionslist = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Integer>> epsilontransitionslist = new ArrayList<ArrayList<Integer>>();
              
    
        for(int i=0;i<noofstates;i++)
        {
            ArrayList<Integer> temptransitions = new ArrayList<Integer>();
            ArrayList<Integer> tempepsilontransitions = new ArrayList<Integer>();

            states.add(i);
            System.out.println("Enter Number of Transitions followed by the transitions for "+i+" :");
            int nooftransitions = s.nextInt();
            for(int k=0;k<nooftransitions;k++)
            {
                temptransitions.add(s.nextInt());
            }
            transitionslist.add(temptransitions);

            System.out.println("Enter Number of Epsilon Transitions followed by the transitions for "+i+" :");
            int noofepsilontransitions = s.nextInt();
            for(int y=0;y<noofepsilontransitions;y++)
            {
                tempepsilontransitions.add(s.nextInt());
            }
            epsilontransitionslist.add(tempepsilontransitions);
        }
        System.out.println("\n");
        for(int i=0;i<noofstates;i++)
        {
            ArrayList<Integer> epsilonclosure  = new ArrayList<Integer>();
            ArrayList<Integer> bfsqueue = new ArrayList<Integer>();
            epsilonclosure.add(i);
            bfsqueue.add(i);
            ArrayList<Integer> particular_eptransitions = epsilontransitionslist.get(i);
            for(int rr=0;rr<particular_eptransitions.size();rr++)
            {
                bfsqueue.add(particular_eptransitions.get(rr));
                epsilonclosure.add(particular_eptransitions.get(rr));
            }

            while(bfsqueue.size()!=0)
            {
                for(int l=0;l<bfsqueue.size();l++)
                {
                    ArrayList<Integer> aa = epsilontransitionslist.get(bfsqueue.get(l));
                    for(int uu=0;uu<aa.size();uu++)
                    {
                        if(!bfsqueue.contains(aa.get(uu)))
                        {
                            bfsqueue.add(aa.get(uu)); 
                            epsilonclosure.add(aa.get(uu));                                               
                        }
                    }
                    bfsqueue.remove(l);
                
                }
                
            }
            System.out.println("Epsilon Closure of "+i+" = "+epsilonclosure.toString());
        }
    }

}