import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;
import java.io.FileWriter;

public class DistanceMeasurement{

    private static int startLatLngLine=2;
    private static int destinationsLatLngLine=4;
    private static Scanner scn;
    private static String filename="data.sj";
    private static LatLng startLatLng;
    private static ArrayList<LatLng> destinationsLatLng=new ArrayList<>();

    public static void main(String[] args)
    {
        String filename=args[0];
        if(args.length>1) startLatLngLine=Integer.valueOf(args[1]);
        if(args.length>2) destinationsLatLngLine=Integer.valueOf(args[2]);

        System.out.println("=======Reading from file source: "+filename+"============");
        readData(filename);
    }

    public static void readData(String filename){
        File file=new File(filename);
        try{
            scn =new Scanner(file);
            int i=1;
            String data=null;
            while(scn.hasNextLine())
            {
                data=scn.nextLine();
                if(i==startLatLngLine)
                {
                    String[] splitData=data.split(",");
                    double lat=Double.valueOf(splitData[0]);
                    double lng=Double.valueOf(splitData[1]);
                    startLatLng=new LatLng(lat,lng);
                }
                else if(i>=destinationsLatLngLine)
                {
                    String[] splitData=data.split(",");
                    double lat=Double.valueOf(splitData[0]);
                    double lng=Double.valueOf(splitData[1]);
                    LatLng dataLatLng=new LatLng(lat,lng);
                    destinationsLatLng.add(dataLatLng);
                }
                data=null;
                i++;
            }


            System.out.println("Start Lat,Lng = "
            +startLatLng.getLatitude()+","+startLatLng.getLongitude());
            i=0;
            for(LatLng coordinate : destinationsLatLng)
            {
                i++;
                System.out.println("Destination Lat,Lng "+i+" = "
                +coordinate.getLatitude()+","+coordinate.getLongitude());
            }

            countingData();

        }catch(Exception e)
        {
            System.out.println(e);
        }
    }

    public static void countingData()
    {
        System.out.println("============Start Measurement=============");
        System.out.println("========Spherical Law of Cosines==========");
         try{    
           FileWriter fw=new FileWriter("SLOC Result.sj");
           fw.write("Spherical Law of Cosines Distance Measurment\n\n");
           fw.write("[Start Coordinate]\n");
           fw.write(startLatLng.getLatitude()+","+startLatLng.getLongitude()+"\n\n");

           fw.write("[Measurement Result For Each Destination Coordinate]\n");
           int i=0;
            for(LatLng coordinate : destinationsLatLng)
            {
                i++;
                double slocResult=new SphericalLawOfCosines()
                    .setStartLatLng(startLatLng.getLatitude(),startLatLng.getLongitude())
                    .setDestinationLatLng(coordinate.getLatitude(),coordinate.getLongitude())
                    .setUnit("Km")
                    .setRounded(2,RoundingMode.HALF_UP)
                    .countDistanceBetween()
                    .getDistanceBetween();
                fw.write("Destination "+i+" Lat,Lng ("
                +coordinate.getLatitude()+","+coordinate.getLongitude()+") = "+slocResult+"Km\n");
            }    
           fw.close();    
           System.out.println("============== Done ===================");
          }catch(Exception e){System.out.println(e);}

          System.out.println("============Start Measurement=============");
        System.out.println("======== Haversine Formula ==========");
         try{    
           FileWriter fw=new FileWriter("Haversine Result.sj");
           fw.write("Haversine Formula Distance Measurment\n\n");
           fw.write("[Start Coordinate]\n");
           fw.write(startLatLng.getLatitude()+","+startLatLng.getLongitude()+"\n\n");

           fw.write("[Measurement Result For Each Destination Coordinate]\n");
           int i=0;
            for(LatLng coordinate : destinationsLatLng)
            {
                i++;
                double slocResult=new HaversineFormula()
                    .setStartLatLng(startLatLng.getLatitude(),startLatLng.getLongitude())
                    .setDestinationLatLng(coordinate.getLatitude(),coordinate.getLongitude())
                    .setUnit("Km")
                    .setRounded(2,RoundingMode.HALF_UP)
                    .countDistanceBetween()
                    .getDistanceBetween();
                fw.write("Destination "+i+" Lat,Lng ("
                +coordinate.getLatitude()+","+coordinate.getLongitude()+") = "+slocResult+"Km\n");
            }    
           fw.close();    
           System.out.println("============== Done ===================");
          }catch(Exception e){System.out.println(e);}
    }
}


class LatLng{
    private double latitude,longitude;
    public LatLng(double lat,double lng)
    {
        super();
        latitude=lat;
        longitude=lng;
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude()
    {
        return longitude;        
    }
}

class SphericalLawOfCosines
{

    private double startLatitude, startLongitude, destinationLatitude, destinationLongitude;
    private String Units=null;
    private BigDecimal bigDecimal;
    private int roundDecimal=-1;
    private RoundingMode roundingMode=null;
    private static double distanceBetween=0;

    public SphericalLawOfCosines()
    {
        super();
    }

    public SphericalLawOfCosines setStartLatLng(double startLatitude, double startLongitude)
    {
        this.startLatitude=Math.toRadians(startLatitude);
        this.startLongitude=Math.toRadians(startLongitude);
        return this;
    }

    public SphericalLawOfCosines setDestinationLatLng(double destinationLatitude, double destinationLongitude)
    {
        this.destinationLatitude=Math.toRadians(destinationLatitude);
        this.destinationLongitude=Math.toRadians(destinationLongitude);
        return this;
    }

    public SphericalLawOfCosines setUnit(String Units)
    {
        this.Units=Units;
        return this;
    }

    public SphericalLawOfCosines setRounded(int roundDecimal, RoundingMode roundingMode)
    {
        this.roundDecimal=roundDecimal;
        this.roundingMode=roundingMode;
        return this;
    }

    public SphericalLawOfCosines countDistanceBetween()
    {
        final double R=6371000;                 // Earth Mean Radius 6,371 Km (6,371000 m)
        double x1,x2,y1,y2,differenceY;

        x1=startLatitude;                       // x1 variable : value of starting latitude in radians
        x2=destinationLatitude;                 // x2 variable : value of destination latitude in radians
        y1=startLongitude;                      // y1 variable : value of statring longitude in radians
        y2=destinationLongitude;                // y2 variable : value of destination longitude in radians
        differenceY=y2-y1;                      // differenceY variable : value of difference between y1 and y2 variable

        //Calculating distance with Spherical Of Cosines Formula
        distanceBetween=Math.acos( (Math.sin(x1)*Math.sin(x2)+ Math.cos(x1)*Math.cos(x2)) * Math.cos(differenceY) ) * R;


        //Modifier request result with Units
        UnitsConversion();

        //Modifier request with Rounded
        RoundingResult();


        return this;
    }

    private void UnitsConversion()
    {
        if(Units.equals("Km") || Units.equals("km"))
        {
            distanceBetween=distanceBetween/1000;
        }
    }

    private void RoundingResult()
    {
        if(roundDecimal!=-1 && roundingMode!=null)
        {
            bigDecimal=new BigDecimal(distanceBetween);
            bigDecimal=bigDecimal.setScale(roundDecimal,roundingMode);
            distanceBetween=bigDecimal.doubleValue();
        }
    }

    public double getDistanceBetween()
    {

        return distanceBetween;
    }
}

class HaversineFormula {
    private double startLatitutde, startLongitude, destinationLatitude, destinationLongitude;
    private String Units = null;
    private BigDecimal bigDecimal;
    private int roundDecimal = -1;
    private RoundingMode roundingMode = null;
    private static double distanceBetween = 0;

    public HaversineFormula() {
        super();
    }

    public HaversineFormula setStartLatLng(double lat, double lng) {
        this.startLatitutde = Math.toRadians(lat);
        this.startLongitude = Math.toRadians(lng);
        return this;
    }

    public HaversineFormula setDestinationLatLng(double destinationLatitude, double destinationLongitude) {
        this.destinationLatitude = Math.toRadians(destinationLatitude);
        this.destinationLongitude = Math.toRadians(destinationLongitude);
        return this;
    }

    public HaversineFormula setUnit(String Units) {
        this.Units = Units;
        return this;
    }

    public HaversineFormula setRounded(int roundDecimal, RoundingMode roundingMode) {
        this.roundDecimal = roundDecimal;
        this.roundingMode = roundingMode;
        return this;
    }

    public HaversineFormula countDistanceBetween() {
        final double R = 6371000;
        double x1, x2, y1, y2, dLat, dlon, a, c, d;
        x1 = startLatitutde;
        y1 = startLongitude;
        x2 = destinationLatitude;
        y2 = destinationLongitude;

        dLat = (x2 - x1);
        dlon = (y2 - y1);

        a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(x1) * Math.cos(x2) *
            Math.sin(dlon / 2) * Math.sin(dlon / 2);
        c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        distanceBetween = R * c;

        UnitsConversion();

        RoundingResult();

        return this;
    }

    private void UnitsConversion() {
        if (Units.equals("Km") || Units.equals("Km")) {
            distanceBetween = distanceBetween / 1000;
        } else {

        }
    }

    public void RoundingResult() {
        if (roundDecimal != -1 && roundingMode != null) {
            bigDecimal = new BigDecimal(distanceBetween);
            bigDecimal = bigDecimal.setScale(roundDecimal, roundingMode);
            distanceBetween = bigDecimal.doubleValue();
        }
    }

    public static double getDistanceBetween() {
        return distanceBetween;
    }
}