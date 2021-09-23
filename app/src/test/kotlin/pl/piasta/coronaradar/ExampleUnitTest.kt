package pl.piasta.coronaradar

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.ints.shouldBeExactly

class ExampleUnitTest : BehaviorSpec({

    given("integer value of 3") {
        val x = 3

        `when`("multiply it by 2") {
            val res = x.times(2)

            then("result is 6") {
                res.shouldBeExactly(6)
            }
        }

        `when`("subtract it by 2") {
            val res = x.minus(2)

            then("result should be 1") {
                res.shouldBeExactly(1)
            }
        }
    }
})