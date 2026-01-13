package joptsimple.internal;

class Row {
   final String option;
   final String description;

   Row(String option, String description) {
      this.option = option;
      this.description = description;
   }

   @Override
   public boolean equals(Object that) {
      if (that == this) {
         return true;
      } else if (that != null && this.getClass().equals(that.getClass())) {
         Row other = (Row)that;
         return this.option.equals(other.option) && this.description.equals(other.description);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.option.hashCode() ^ this.description.hashCode();
   }
}
