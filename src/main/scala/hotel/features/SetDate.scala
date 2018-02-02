package hotel.features

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

/**
  * Created by yu_wei on 2017/12/12.
  */
object SetDate {

   /**
     * @param num : 0:today, -1:yesterday, -2:the day before yesterday, 1:tomorrow, 2:the day after tomorrow
     * @return : datetime
     */
   def getDatetime(num: Int) = {
      val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
      if (num == 0) {
         val now = new Date()
         val today = dateFormat.format(now)
         today
      } else if (num != 0) {
         val cal: Calendar = Calendar.getInstance()
         cal.add(Calendar.DATE, num)
         val datetime = dateFormat.format(cal.getTime)
         datetime
      }
   }

}
