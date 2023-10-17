import java.util.Random;
import java.util.ArrayList;
import java.io.*;

/**
 * Simple GA for the OneMax problem.
 * 
 */
public class GA
{
    /**
     * Population (number of households) per area.
     */
    private static final int BITS = 7;

    /**
     * The population size.
     */
    //private static double accuracy;
    private static final int POPULATION_SIZE =100;

    private static final double training_data  =POPULATION_SIZE*.7;

    /**
     * The number of generations.
     */
    private static final int MAX_GENERATION = 20;

    /**
     * Probability of the mutation operator.
     */
    private static final double MUTATION_PROBABILITY = 0.4 ;

    /**
     * Probability of the crossover operator.
     */
    private static final double CROSSOVER_PROBABILITY = 0.9;

    /**
     * Number of individuals saved by elitism.
     */
    private static final int ELITE_SIZE = 1;

    /**
     * Number of individuals in the tournament.
     */
    private static final int TOURNAMENT_SIZE = 2;

    /**
     * Random number generation.
     */
    private static Random random = new Random();

    /**
     * The current population;
     */
    private static boolean[][] population = new boolean[POPULATION_SIZE][BITS];

    /**
     * Fitness values of each individual of the population.
     */
    private static int[] fitness = new int[POPULATION_SIZE];
   // private static int[] newFitness = new int[POPULATION_SIZE];


    // public void load(String filename) throws IOException {
    // BufferedReader reader = new BufferedReader(new FileReader(filename));

    // ArrayList<Boolean> col1 = new ArrayList<>();
    // ArrayList<Boolean> col2 = new ArrayList<>();
    // ArrayList<Boolean> col3 = new ArrayList<>();
    // ArrayList<Boolean> col4 = new ArrayList<>();
    // ArrayList<Boolean> col5 = new ArrayList<>();
    // ArrayList<Boolean> col6 = new ArrayList<>();
    // ArrayList<Boolean> col7 = new ArrayList<>();

    // String line = null;
    // // skip the first line (column names)
    // reader.readLine();
    // int skipped = 0;
    // while ((line = reader.readLine()) != null  ) {
    // if(!(line.contains("NA")))
    // {
    // String[] split = line.split(",");

    // col1.add(Boolean.valueOf(split[23]));
    // col2.add(Boolean.valueOf(split[24]));
    // col3.add(Boolean.valueOf(split[25]));
    // col4.add(Boolean.valueOf(split[26]));
    // col5.add(Boolean.valueOf(split[27])); 
    // col6.add(Boolean.valueOf(split[28])); 
    // col7.add(Boolean.valueOf(split[29])); 

    // }
    // else{
    // skipped++;
    // System.out.println("skipped number " + skipped);}
    // }
    // noOfAreas = col1.size();
    // households = col1.toArray(new Integer[noOfAreas]);
    // imitator = col2.toArray(new Double[noOfAreas]);
    // }

    public static ArrayList<boolean[]> read(String input) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(input));
        ArrayList<boolean[]> data = new ArrayList<>();

        reader.readLine();

        String line;
        while((line = reader.readLine()) != null) {
            String[] values = line.split(","); // ["1", "1", "1", "1"]
            boolean[] ints = new boolean[values.length];

            for (int i =0; i < values.length; i++) {
                if(values[i] .equals("1")) {
                    ints[i] = true;
                } else {
                    ints[i] = false;
                }
            }

            data.add(ints);
        }

        reader.close();

        return data;
    }

    public static boolean compare(boolean[] rule, boolean[] instance){
        for (int i = 0; i < instance.length - 1; i++) {
            if(rule[i]==instance[i]){

            }
            else{
                return false;
            }

        }
        return true;
    }

    public static boolean predict(boolean[] rule, boolean[] instance) {
        int position = 0;
        boolean match;
        boolean A =(instance[instance.length-1]);
        boolean B= (rule[rule.length-1]);
        if(compare(rule, instance)){
            return A == B;
        }
        else{
            return A == !(B);
        }

        // if rule == instance match = true else match = false
        //            if(rule[i]==instance[i]){
        //                match=true;
        //
        //
        //            }
        //            else{
        //
        //                match=false;
        //
        //                if(A==!B){
        //                    return true;
        //                }
        //                else{
        //                    return false;
        //                }
        //            }
        //
        //        }
        // if match get last boolean of instance else negate last value of rule
        // return if prediction matches last boolean

        //        if(match=true){
        //
        //
        //            if(A=B){
        //                return true;
        //            }
        //            else{
        //                return false;
        //            }
        //        }
        //        else{
        //
        //
        //            B= !(rule[rule.length-1]);
        //            if(A=B){
        //                return true;
        //            }
        //            else{
        //                return false;
        //            }//flip the rule,
        //        }

    }

    public static void main(String[] args){
        run();

    }

    /**
     * Starts the execution of the GA.
     */
    public static void run() {
        ArrayList<boolean[]> data= new ArrayList<>();
        try {
            data =  read("Dataset.csv");

        }
        catch(Exception e) {
            System.out.println("Error");
        }
        //--------------------------------------------------------------//
        // initialises the population                                   //
        //--------------------------------------------------------------//
        initialise();

        //--------------------------------------------------------------//
        // evaluates the propulation                                    //
        //--------------------------------------------------------------//

        evaluate(data);

        for (int g = 0; g < MAX_GENERATION; g++) {
            // prints the information regarding the evolution
            System.out.println(String.format("Generation %3d: best fitness=%d " , g, fitness[ findBestN(1)[0] ]));

            //----------------------------------------------------------//
            // creates a new population                                 //
            //----------------------------------------------------------//

            boolean[][] newPopulation = new boolean[POPULATION_SIZE][BITS];
            // index of the current individual to be created
            int current = 0;
            //----------------------------------------------------------//
            // performs elitism                                         //
            //----------------------------------------------------------//
            int[] elite = findBestN(ELITE_SIZE);

            for (int i = 0; i < ELITE_SIZE; i++) {
                newPopulation[current] = (boolean[]) population[ elite[i] ].clone();
                current += 1;
            }


            while (current < POPULATION_SIZE) {
                double probability = random.nextDouble();

                // should we perform mutation?
                if (probability <= MUTATION_PROBABILITY || (POPULATION_SIZE - current) == 1) {
                    int parent = tournament();

                    boolean[] offspring = pointMutation(parent);
                    // copies the offspring to the new population
                    newPopulation[current] = offspring;
                    current += 1;
                }
                // otherwise we perform a crossover
                else {
                    int first = tournament();
                    int second = tournament();

                    boolean[][] offspring = onePointCrossover(first, second);
                    // copies the offspring to the new population
                    newPopulation[current] = offspring[0];
                    current += 1;
                    newPopulation[current] = offspring[1];
                    current += 1;
                }
            }

            population = newPopulation;

            //----------------------------------------------------------//
            // evaluates the new population                             //
            //----------------------------------------------------------//
            evaluate(data);
        }

        int best = findBestN(1)[0];

        // prints the value of the best individual
        System.out.println("Best individual: ");
        System.out.print("[");

        for (int i = 0; i < BITS; i++) {
            if (population[best][i]) {
                System.out.print(" 1");
            }
            else {
                System.out.print(" 0");
            }
        }

        System.out.println(" ]");
    }
    // loop over data

    // predict(population[i], data.get(j)
    // if true
    // fitness++
    /**
     * Calculates the fitness of each individual.
     */
    private static void evaluate(ArrayList<boolean[]> data) {
        for (int i = 0; i < training_data; i++) {
            int active = 0;
            for (int j = 0; j < training_data; j++){

                // loop over data
               // accuracy = (active)*100;
                if(predict(population[i], data.get(j))){
                    active++;
                  //  accuracy = fitness[i];


                }

                // if true
                // the++

            }

           // fitness[i] = active;
           // accuracy = fitness[i]; //randomly divides by 3??
            fitness[i] = active;
          //  accuracy = ((double) fitness[i]/POPULATION_SIZE*100.0);
         //   accuracy = active;
            //   System.out.println("a");

            //            for (int i = 0; i < population.length; i++) { //for everyrow in Population (100)
            //                int active = 0;
            //
            //                for (int j = 0; j < population[i].length; j++) {//In a individual Pop_size there are 25 elements,[0,0] to [0,25] then [1,0] until [25,25]
            //                    if (population[i][j]) { //if theres a true value, add 1 to active
            //                        active++;
            //                    }
            //                }
            //                fitness[i] = active;
            //            }

            // // for (int i = 0; i < population.length; i++) { //for everyrow in Population (100)
            // // int active = 0;

            // // //instantiate rule
            // // for (int j = 0; i < population[i].length; j++) {
            // // if(col1.add(Boolean.valueOf(split[23])) && col2.add(Boolean.valueOf(split[24]))&& col3.add(Boolean.valueOf(split[25]))&& col4.add(Boolean.valueOf(split[26]))&& col5.add(Boolean.valueOf(split[27]))&&  col6.add(Boolean.valueOf(split[28]))==true //Random rule 
            // // ) //if values match 
            // // {
            // // if(col4.add(Boolean.valueOf(split[26]=Rule[i]))//if predictions =targets
            // // {
            // // active++;

            // // }
            // // }
            // // else{ // doesnt match
            // // !Rule[i]-1 ;//negate the last rule (14 day increase)
            // // if(col4.add(Boolean.valueOf(split[26]=Rule[i]{ //if predictions =targets
            // // active++;
            // // }
            // // }
            // // }
            // // }
            // // fitness[i]=active/rows;

        }
    }

    /**
     * Returns the index of the selected parent using a tournament selection.
     * 
     * @return the index of the selected parent using a tournament selection.
     */

	private static int tournament() {
		// Array to keep track of which individuals have been selected
		boolean[] selected = new boolean[POPULATION_SIZE];

		// Array to hold the individuals selected for the tournament
		int[] tournament = new int[TOURNAMENT_SIZE];

		// Select individuals for the tournament
		for (int i = 0; i < TOURNAMENT_SIZE; i++) {
			int chosen = -1;

			// Keep trying until we find an individual that hasn't been selected yet
			while (chosen == -1) {
				// Randomly select a potential individual
				int potential = random.nextInt(POPULATION_SIZE);

				// If this individual hasn't been selected yet, choose it
				if (!selected[potential]) {
					chosen = potential;
					tournament[i] = chosen;

					// Mark this individual as selected so we don't select it again
					selected[chosen] = true;
				}
			}
		}

		// Assume the first individual is the winner
		int winner = 0;

		// Check each individual in the tournament to see if they're more fit
		for (int i = 1; i < TOURNAMENT_SIZE; i++) {
			if (fitness[tournament[winner]] < fitness[tournament[i]]) {
				// If this individual is more fit, it's the new winner
				winner = i;
			}
		}

		// Return the most fit individual from the tournament
		return tournament[winner];
	}

    /**
     * Initialises the population.
     */
    private static  void initialise() {
        population= new  boolean[POPULATION_SIZE][BITS];
        fitness= new int[POPULATION_SIZE];
        for (int i = 0; i < population.length; i++) {
            population[i] = new boolean[BITS]; // 25 empty spaces that can be booleans

            for (int j = 0; j < population[i].length; j++) {
                population[i][j] = random.nextBoolean(); //fills the array with True or falses (25 across, 100 down)
            }
        }
    }
    private static boolean[][] onePointCrossover(int first, int second) {// 1st and second parents
        boolean[][] offspring = new boolean[2][BITS]; // 2 empty arrays of length BITS
        int point = random.nextInt(BITS);

        for (int i = 0; i < BITS; i++) {
            if (i == point) {
                int k = first;
                first = second;
                second = k;
            }

            offspring[0][i] = population[first][i];//first is permenantantly switched to second in the if statement, carrys on indefiently
            offspring[1][i] = population[second][i];//second is permenantantly switched to first in the if statement, carrys on indefiently
        }

        return offspring;
    }
    /**
     * Point mutation operator.
     * 
     * @param parent index of the parent individual from the population.
     * 
     * @return the generated offspring.
     */
    //only the point WILL change 
    private static boolean[] pointMutation(int parent) { // parent = individual(between 1 and 100 )
        boolean[] offspring = new boolean[BITS]; //25 elements long
        int point = random.nextInt(BITS); //choose something between 0 to BITS

        for (int i = 0; i < BITS; i++) {
            if (i == point) {
                offspring[i] = !population[parent][i];// If i equals the point,a point in the population(which is comprised of POP_SIZE=parents, and BITS=i) is negated ),
                //so when parent =3 (3rd parent in a list of 100)if i =4, offspring(4) = ![3][4]
            }
            else {
                offspring[i] = population[parent][i]; // If i doesnt equal the point,copy
            }
        }

        return offspring;
    }

    // /**
     // * Bit string mutation operator.
     // * 
     // * @param parent index of the parent individual from the population.
     // * 
     // * @return the generated offspring.
     // */
    // private static boolean[] bitStringMutation(int parent) { //all have a chance(1/25) of change
        // boolean[] offspring = new boolean[BITS];
        // double chance = 1.0 / BITS;

        // for (int i = 0; i < BITS; i++) {
            // double probability = random.nextDouble();//always between 0 and 1, always generates a new proabability every loop,
            // if (probability < chance) {
                // offspring[i] = !population[parent][i];
            // } else {
                // offspring[i] = population[parent][i];
            // }
        // }

        // return offspring;
    // }

    /**
     * One-point crossover operator. Note that the crossover generates two offsprings,
     * so both current and current+1 position in the new population must be filled.
     * 
     * @param first index of the first parent individual from the population.
     * @param second index of the second parent individual from the population.
     * 
     * @return the generated offspring.
     */


    // /**
     // * Two-point crossover operator. Note that the crossover generates two offsprings,
     // * so both current and current+1 position in the new population must be filled.
     // * 
     // * @param first index of the first parent individual from the population.
     // * @param second index of the second parent individual from the population.
     // * 
     // * @return the generated offspring.
     // */
    // private static boolean[][] twoPointCrossover(int first, int second) {// 1st and second parents
        // boolean[][] offspring = new boolean[2][BITS];
        // int point1 = 0;
        // int point2 = 0;

        // while (point1 == point2) {
            // point1 = random.nextInt(BITS - 1);
            // // makes sure that point2 happens after point 1
            // point2 = (point1 + 1) + random.nextInt(BITS - (point1 + 1));
        // }

        // for (int i = 0; i < BITS; i++) {
            // if (i == point1 || i == point2) {
                // int k = first;
                // first = second;
                // second = k;
            // }

            // offspring[0][i] = population[first][i];
            // offspring[1][i] = population[second][i];

        // }

        // return offspring;
    // }

    /**
     * Returns the best n individuals in the current population.
     * 
     * @return the best n individuals in the current population.
     */
    private static int[] findBestN(int n) {
        // indexes of the best individuals
        int[] elite = new int[n];
        // copy of the fitness values
        int[] current = (int[]) fitness.clone(); //current is current fitness array

        for (int e = 0; e < n; e++) {
            int best = 0;

            for (int i = 1; i < POPULATION_SIZE; i++) {//
                if (current[best] < current[i]) {
                    best = i;
                }
            }

            elite[e] = best;
            // avoids seleting the same individual again,cuurent is getting destroyed at the loop continues  int 3 finds the top 3 fitness
            current[best] = 0;
        }

        return elite;
    }
}