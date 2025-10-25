package com.dreamyducks.navcook.SampleData

import com.dreamyducks.navcook.network.Ingredient
import com.dreamyducks.navcook.network.Recipe
import com.dreamyducks.navcook.network.Step

val FriedRice_Recipe =
    Recipe(
        id = 4,
        title = "Simple Fried Rice",
        thumbnail = "https://drive.google.com/uc?export=download&id=1DCBZQoGxMgTiunLYFwAub1tKPLU7XQch",
        ingredients =
            listOf(
                Ingredient(
                    name = "Rice",
                    unit = "grams",
                    amount = 300
                ),
                Ingredient(name = "Green Onion", unit = "grams", amount = 30),
                Ingredient(
                    name = "Egg",
                    unit = "eggs",
                    amount = 2
                ),
                Ingredient(
                    name = "Vegetable Oil",
                    unit = "Tablespoons",
                    amount = 2
                ),
                Ingredient(name = "Soy Sauce", unit = "Tablespoon", amount = 1)
            ),
        steps = listOf(
            Step(
                step = "1",
                title = "Prepare eggs and toppings",
                image = "1-osZyrJYDQixIC3thqdjjuOzD4vozLvs",
                ingredients = listOf("Green Onions", "Eggs"),
                description = "Beat the eggs, and chop the green onions."
            ),
            Step(
                step = "2",
                title = "Prepare rice",
                image = "1RdMRaSy9w06fh96CWf03SuHCt08kWS6e",
                ingredients = listOf("Rice", "Vegetable Oils"),
                description = "In a bowl, pour rice and one Tablespoon of vegetable oil and mix well."
            ),
            Step(
                step = "3",
                title = "Mix the rice and eggs",
                image = null,
                ingredients = null,
                description = "In the same bowl, mix the beated eggs from the step 1."
            ),
            Step(
                step = "4",
                title = "Start baking",
                image = "1mgfxGlLgKQfN6yKjDNtVoMKPOvp-aGh7",
                ingredients = null,
                description = "In a pan, add one Tablespoon of vegetable oil and preheat it in high.After the preheat, pour the rice in the pan and spread ."
            ),
            Step(
                step = "5",
                title = "Add Toppings",
                image = "1O4HBG9jQiFW7XafoWSAK1LSaJl9VjN7V",
                ingredients = null,
                description = "Add toppings to the pan and mix well . Make a space in the center of pan and pour soy sauce."
            )
        ),
        tags = "Fried Rice\\Chinese\\Japanese\\Rice\\Vegetable\\Vegetarian"
    )