
public class calendarDriver {

    public static void main(String[] args) {
        calendarConnector cC = new calendarConnector(0);
        cC.getAppts("appointment", "June 1 2015", "June 21 2015");
        cC.getDistance("119 rockport crescent");
        System.out.println(cC);

    }
}