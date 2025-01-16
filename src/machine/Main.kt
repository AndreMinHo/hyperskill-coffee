package machine

class CoffeeMachine(
    private val stockMap: MutableMap<String, Int> = mutableMapOf(
        "water" to 400,
        "milk" to 540,
        "beans" to 120,
        "cups" to 9
    ),
    private var money: Int = 550,
    var state: State = State.OFF
) {
    enum class State() {
        OFF,
        MENU,
        BUY_MENU,
        FILL_WATER,
        FILL_MILK,
        FILL_BEANS,
        FILL_CUPS
    }

    private val recipeMap = mapOf(
        "espresso" to mapOf(
            "water" to 250,
            "milk" to 0,
            "beans" to 16,
            "cups" to 1
        ),
        "latte" to mapOf(
            "water" to 350,
            "milk" to 75,
            "beans" to 20,
            "cups" to 1
        ),
        "cappuccino" to mapOf(
            "water" to 200,
            "milk" to 100,
            "beans" to 12,
            "cups" to 1
        )
    )

    private val priceMap = mapOf(
        "espresso" to 4,
        "latte" to 7,
        "cappuccino" to 6
    )

    fun start() {
        state = State.MENU
    }

    fun getInput(input: String) {
        when (state) {
            State.MENU -> controlMainMenu(input)
            State.BUY_MENU -> buy(input)
            State.FILL_WATER -> fillWater(input)
            State.FILL_MILK -> fillMilk(input)
            State.FILL_BEANS -> fillBeans(input)
            State.FILL_CUPS -> fillCups(input)
            else -> return
        }
    }

    fun showPromptText() {
        when (state) {
            State.MENU -> 
                println("Write action (buy, fill, take, remaining, exit): ")
            State.BUY_MENU -> 
                println("\nWhat do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu: ")
            State.FILL_WATER -> println("\nWrite how many ml of water you want to add: ")
            State.FILL_MILK -> println("Write how many ml of milk you want to add: ")
            State.FILL_BEANS -> println("Write how many grams of coffee beans you want to add: ")
            State.FILL_CUPS -> println("Write how many disposable cups you want to add: ")
        }
    }
    
    private fun controlMainMenu(input: String) {
        when (input) {
            "buy" -> state = State.BUY_MENU
            "fill" -> state = State.FILL_WATER
            "take" -> takeMoney()
            "remaining" -> printStats()
            "exit" -> state = State.OFF
            else -> println("Invalid Command")
        }
    }
    
    


    private fun printStats() {
        println(
            """
                
            The coffee machine has:
            ${stockMap["water"]} ml of water
            ${stockMap["milk"]} ml of milk
            ${stockMap["beans"]} g of coffee beans
            ${stockMap["cups"]} disposable cups
            ${'$'}$money of money
            
            """.trimIndent()
        )
    }

    private fun buy(input: String) {
        try {
            if (input == "back") {
                println()
                state = State.MENU
                return
            }
            when (input.toInt()) {
                1 -> manageResources("espresso")
                2 -> manageResources("latte")
                3 -> manageResources("cappuccino")
                else -> println("Invalid command")
            }
        } catch (e: NumberFormatException) {
            println("Please enter a valid command")
        }

    }

    private fun manageResources(selected: String) {
        val requiredResources = recipeMap[selected]
        if (requiredResources == null) {
            println("Invalid selection")
            return
        }

        val insufficientIngredients = requiredResources.filter { (ingredient, amount) ->
            amount > stockMap.getOrDefault(ingredient, 0)
        }.keys

        if (insufficientIngredients.isNotEmpty()) {
            insufficientIngredients.forEach { ingredient ->
                when (ingredient) {
                    "water" -> println("Sorry, not enough water!")
                    "milk" -> println("Sorry, not enough milk!")
                    "beans" -> println("Sorry, not enough coffee beans!")
                    "cups" -> println("Sorry, not enough disposable cups!")
                }
            }
            println()
            state = State.MENU
            return
        }

        println("I have enough resources, making you a coffee!\n")

        requiredResources.forEach { (key, value) ->
            stockMap[key] = stockMap.getOrDefault(key, 0) - value
        }
        money += priceMap.getOrDefault(selected, 0)
        state = State.MENU
    }
    
    private fun fillWater(input: String) {
        addResource("water", input)
        state = State.FILL_MILK
    }
    private fun fillMilk(input: String) {
        addResource("milk", input)
        state = State.FILL_BEANS
    }
    private fun fillBeans(input: String) {
        addResource("beans", input)
        state = State.FILL_CUPS
    }
    private fun fillCups(input: String) {
        addResource("cups", input)
        println()
        state = State.MENU
    }

    private fun addResource(resource: String, amount: String) {
        val intAmount = amount.toIntOrNull()
        if (intAmount != null) {
            stockMap[resource] = stockMap.getOrDefault(resource, 0) + intAmount
        } else {
            println("Invalid number")
        }
    }

    private fun takeMoney() {
        println("\nI gave you \$$money\n")
        money = 0
    }

}

fun main() {
    val machine = CoffeeMachine()
    machine.start()
    while (machine.state != CoffeeMachine.State.OFF) {
        machine.showPromptText()
        machine.getInput(readln())
    }

}
