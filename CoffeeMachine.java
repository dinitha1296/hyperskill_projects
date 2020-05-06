package machine;

import java.util.Scanner;
import java.util.ArrayList;

public class CoffeeMachine {

    private String input;
    private State state = State.INITIATING;
    private Ingredients ingredientsInMachine;
    public boolean run = true;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        CoffeeMachine coffeeMachine = new CoffeeMachine(new int[]{400, 540, 120, 9, 550});

        while (coffeeMachine.run) {
            coffeeMachine.getInput(scanner.next());
        }
    }

    public CoffeeMachine(int[] initialIngredients) {
        ingredientsInMachine = new Ingredients(initialIngredients);
        System.out.println("Write action (buy, fill, take, remaining, exit):");
    }

    public void getInput(String in) {
        this.input = in;
        this.operateMachine();
    }

    private void operateMachine() {

        switch (this.state.toString()) {
            case "INITIATING": this.initiateMachine(); break;
            case "BUYING": this.buyCoffee(); break;
            default: this.fillMachine();
        }
    }

    private void initiateMachine() {
        switch (input) {
            case "buy": this.changeState("BUYING"); break;
            case "fill": this.changeState("F1"); break;
            case "take": this.takeCash(); break;
            case "remaining": this.printWhatsLeft(); break;
            default: run = false;
        }
    }

    private void fillMachine() {

        int fillingCategory = Integer.parseInt(state.toString().substring(1, 2)) - 1;
        int[] add = {0, 0, 0, 0, 0};
        add[fillingCategory] = Integer.parseInt(input);
        ingredientsInMachine.addIngredients(add);

        if (this.state.toString().equals("F4")) {
            this.changeState("INITIATING");
        } else {
            this.changeState("F" +
                    (Integer.parseInt(state.toString().substring(1, 2)) + 1));
        }
    }

    private void printWhatsLeft() {

        int[] ingredients = ingredientsInMachine.getIngredients();
        System.out.println("\nThe coffee machine has:\n" +
                ingredients[0] + " of water\n" +
                ingredients[1] + " of milk\n" +
                ingredients[2] + " of coffee beans\n" +
                ingredients[3] + " of disposable cups\n" +
                ingredients[4] + " of money");

        this.changeState("INITIATING");
    }

    private void takeCash() {

        int cash = ingredientsInMachine.getIngredients()[4];
        int[] reduce = {0, 0, 0, 0, -cash};
        ingredientsInMachine.addIngredients(reduce);
        System.out.println("I gave you " + cash);

        this.changeState("INITIATING");
    }

    private void buyCoffee() {

        CoffeeType coffeeType = CoffeeType.CAPPUCCINO;
        try {
            coffeeType.setCoffeeType(Integer.parseInt(input));
            if (ingredientsInMachine.checkAvailability(coffeeType.getIngredients())) {
                System.out.println("I have enough resources, making you a coffee!");
                ingredientsInMachine.addIngredients(coffeeType.getIngredients());
            } else {
                System.out.println("Sorry, not enough " +
                        ingredientsInMachine.returnNonAvailable(coffeeType.getIngredients()) +
                        "!");
            }
        } catch (NumberFormatException e) {}
        this.changeState("INITIATING");

    }

    private void changeState(String newState) {
        this.state = State.valueOf(newState);
        switch (newState) {
            case "INITIATING": System.out.println("\nWrite action (buy, fill, take, remaining, exit):"); break;
            case "BUYING": System.out.println("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino:"); break;
            case "F1": System.out.println("\nWrite how many ml of water do you want to add: "); break;
            case "F2": System.out.println("Write how many ml of milk do you want to add: "); break;
            case "F3": System.out.println("Write how many grams of coffee beans do you want to add:"); break;
            case "F4": System.out.println("Write how many disposable cups of coffee do you want to add: "); break;
        }
    }
}

enum State {
    INITIATING, BUYING, F1, F2, F3, F4;
}

enum CoffeeType {

    ESPRESSO (1), LATTE (2), CAPPUCCINO (3);

    public Ingredients ingredients;
    private int i;
    private final int[][] coffeeTypeIngredients = {{-250, 0, -16, -1, 4}, {-350, -75, -20, -1, 7}, {-200, -100, -12, -1, 6}};

    CoffeeType(int i) {
        this.i = i;
        this.ingredients = new Ingredients(coffeeTypeIngredients[i - 1]);
    }

    public void setCoffeeType(int type) {
        this.i = type;
    }

    public int[] getIngredients() {
        return coffeeTypeIngredients[i - 1];
    }
}

class Ingredients {

    private int[] ingredients;
    private static final String[] ingredientNames = {"water", "milk", "coffee", "cups", "cash"};

    public Ingredients(int[] ingredients) {
        this.ingredients = ingredients;
    }

    public int[] getIngredients() {
        return ingredients;
    }

    public void addIngredients(int[] ingredients) {
        for (int x = 0; x < 5; x++) {
            this.ingredients[x] += ingredients[x];
        }
    }

    public boolean checkAvailability(int[] ingredients) {
        boolean available = true;
        for (int i = 0; i < 4; i ++) {
            available &= this.getIngredients()[i] >= Math.abs(ingredients[i]);
        }
        return available;
    }

    public String returnNonAvailable(int[] ingredients) {
        ArrayList<String> nonAvailable = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            if (this.ingredients[i] >= Math.abs(ingredients[i])) nonAvailable.add(ingredientNames[i]);
        }
        String message = nonAvailable.get(0);
        if (nonAvailable.size() > 1) {
            for (int j = 1; j < nonAvailable.size(); j++) {
                message += (j == nonAvailable.size() - 1) ? " and " : " , ";
                message += nonAvailable.get(j);
            }
        }
        return message;
    }
}