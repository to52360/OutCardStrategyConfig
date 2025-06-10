package lin.bean

import javafx.beans.property.SimpleDoubleProperty

class WeightCard(){

     constructor(cardId: String, name: String, weight: Double, powerWeight: Double, changeWeight: Double) : this() {
         this.cardId = cardId
         this.name = name
         this.weight = weight
         this.powerWeight = powerWeight
         this.changeWeight = changeWeight
     }

     var cardId: String = ""

     var name: String = ""


     val weightProperty = SimpleDoubleProperty(0.0)


     val powerWeightProperty = SimpleDoubleProperty(0.0)


     val changeWeightProperty = SimpleDoubleProperty(0.0)

     var weight: Double
         get() = weightProperty.get()
         set(value) {
             weightProperty.set(value)
         }

     var powerWeight: Double
         get() = powerWeightProperty.get()
         set(value) {
             powerWeightProperty.set(value)
         }

     var changeWeight: Double
         get() = changeWeightProperty.get()
         set(value) {
             changeWeightProperty.set(value)
         }

     override fun equals(other: Any?): Boolean {
         if (this === other) return true
         if (other == null || javaClass != other.javaClass) return false

         other as WeightCard

         return cardId == other.cardId
     }

     override fun hashCode(): Int {
         return cardId.hashCode()
     }
 }
