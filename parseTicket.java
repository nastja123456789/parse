import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String json = readFileF("tickets.json");
        try {
            List<Ticket> tickets = parseJsonJ(json);
            calculateMinFlightTime(tickets);
            calculatePriceDifference(tickets);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String readFileF(String filename) {
        StringBuilder content = new StringBuilder();
        try (FileReader fileReader = new FileReader(filename);
             Scanner scanner = new Scanner(fileReader)) {
            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    private static List<Ticket> parseJsonJ(String json) throws Exception {
        List<Ticket> tickets = new ArrayList<>();
        json = json.replace(" ", "").replace("\n", "");
        String ticketsArray = json.substring(json.indexOf("[") + 1, json.indexOf("]"));
        String[] ticketStrings = ticketsArray.split("\\},\\{");
        for (String ticketString : ticketStrings) {
            Ticket ticket = new Ticket();
            String[] fields = ticketString.split(",");
            for (String field : fields) {
                field=field.replace("}","");
                field=field.replace("{","");
                String[] keyValue = field.split(":");
                System.out.println(field);
                String key = keyValue[0].replaceAll("\"", "");
                String value = keyValue[1].replaceAll("\"", "");
//                String value1=keyValue[];
                System.out.println(key+" " + value);
                switch (key) {
                    case "origin":
                        ticket.origin = value;
                        break;
                    case "origin_name":
                        ticket.originName = value;
                        break;
                    case "destination":
                        ticket.destination = value;
                        break;
                    case "destination_name":
                        ticket.destinationName = value;
                        break;
                    case "departure_date":
                        ticket.departureDate = value;
                        break;
                    case "departure_time":
                        System.out.println(value);
                        String value1 = keyValue[2].replace("\"", "");
                        ticket.departureTime=value+":"+value1;
                        break;
                    case "arrival_date":
                        ticket.arrivalDate = value;
                        break;
                    case "arrival_time":
                        String value2 = keyValue[2].replace("\"", "");
                        ticket.arrivalTime=value+":"+value2;
                        break;
                    case "carrier":
                        ticket.carrier = value;
                        break;
                    case "stops":
                        ticket.stops = Integer.parseInt(value);
                        break;
                    case "price":
//                        String val = value.substring(0, value.length() - 2);
                        ticket.price = Integer.parseInt(value);
                        break;
                }
            }
            tickets.add(ticket);
        }
        return tickets;
    }

    private static void calculateMinFlightTime(List<Ticket> tickets) {
        Map<String, Integer> carrierMinDuration = new HashMap<>();
        System.out.println("Минимальное время полета между городами Владивосток и Тель-Авив: ");
        for (Ticket ticket : tickets) {
            if (ticket.origin.equals("VVO") && ticket.destination.equals("TLV")) {
                int duration = getFlightDuration(ticket.departureTime,ticket.arrivalTime);
                String key=ticket.carrier;
                System.out.println(ticket.arrivalTime+" " + ticket.departureTime + " " + ticket.carrier + " " + getFlightDuration(ticket.departureTime,ticket.arrivalTime));
                if (!carrierMinDuration.containsKey(key) || duration<carrierMinDuration.get(key)) {
                    carrierMinDuration.put(key, duration);
                }
            }
        }
        for (Map.Entry<String, Integer> entry : carrierMinDuration.entrySet()) {
            String carrier = entry.getKey();
            int duration = entry.getValue();
            System.out.println(carrier+" " + duration);
            System.out.printf("Минимальное время полета между городами Владивосток и Тель-Авив для авиаперевозчика %s: %d мин%n",
                    carrier, duration);
        }
    }

    private static int getFlightDuration(String departureTime, String arrivalTime) {
        int departureHour = Integer.parseInt(departureTime.split(":")[0]);
        int departureMinute = Integer.parseInt(departureTime.split(":")[1]);
        int arrivalHour = Integer.parseInt(arrivalTime.split(":")[0]);
        int arrivalMinute = Integer.parseInt(arrivalTime.split(":")[1]);
        System.out.println(departureHour+" " + departureMinute);
        int duration = (arrivalHour * 60 + arrivalMinute) - (departureHour * 60 + departureMinute);
        return duration;
    }

    private static void calculatePriceDifference(List<Ticket> tickets) {
        System.out.println("Разница между средней ценой и медианой для полета между городами Владивосток и Тель-Авив: ");
        List<Integer> prices = new ArrayList<>();
        int totalPrices = 0;
        for (Ticket ticket : tickets) {
            if (ticket.origin.equals("VVO") && ticket.destination.equals("TLV")) {
                System.out.println(ticket.price);
                prices.add(ticket.price);
                totalPrices += ticket.price;
            }
        }
        System.out.println(totalPrices+" "+ prices.size());
        int averagePrice = totalPrices / prices.size();
        int medianPrice = calculateMedianPrice(prices);
        int priceDifference = averagePrice - medianPrice;
        System.out.println("Средняя цена: " + averagePrice);
        System.out.println("Медианная цена: " + medianPrice);
        System.out.println("Разница: " + priceDifference);
    }

    private static int calculateMedianPrice(List<Integer> prices) {
        Collections.sort(prices);
        int size = prices.size();
        boolean even = size % 2 == 0;
        if (even) {
            int index1 = size / 2 - 1;
            int index2 = size / 2;
            return (prices.get(index1) + prices.get(index2)) / 2;
        } else {
            int index = size / 2;
            return prices.get(index);
        }
    }

    private static class Ticket {
        private String origin;
        private String originName;
        private String destination;
        private String destinationName;
        private String departureDate;
        private String departureTime;
        private String arrivalDate;
        private String arrivalTime;
        private String carrier;
        private int stops;
        private int price;
    }
}
