package io.pivotal.springxd.processor;

import com.bbn.openmap.proj.Length;
import com.bbn.openmap.proj.coords.LatLonPoint;
import org.springframework.xd.tuple.Tuple;
import org.springframework.xd.tuple.TupleBuilder;

/**
 * Created by lei_xu on 6/7/16.
 */


/**
 * Created by xul15 on 2015/04/30.
 */
public class FlatToJsonConverter {
    private static final double ORIGIN_CENTER_LATITUDE = 41.474937;
    private static final double ORIGIN_CENTER_LONGITUDE = -74.913585;
    private static final double EDGE = 500;
    private static final double ORIGIN_POINT_LATITUDE = 41.479428420783364;
    private static final double ORIGIN_POINT_LONGITUDE = -74.91958021476788;
    private static final LatLonPoint.Double originPoint = new LatLonPoint.Double(ORIGIN_POINT_LATITUDE, ORIGIN_POINT_LONGITUDE);
    private static final String delims = "[,]";


    public FlatToJsonConverter()
    {
    }


    public Tuple process(String payload) {

        String[] tokens = payload.split(delims);

        System.out.println("payload:" + payload);

        double pickupLatitude = java.lang.Double.parseDouble(tokens[9]);
        double pickupLongitude = java.lang.Double.parseDouble(tokens[8]);
        double dropoffLatitude = java.lang.Double.parseDouble(tokens[7]);
        double dropoffLongitude = java.lang.Double.parseDouble(tokens[6]);
        String dropoffDatetime = tokens[3];
        String pickupDatetime = tokens[2];
        String route = null;

        try
        {
            route = generateRoute(pickupLatitude, pickupLongitude, dropoffLatitude, dropoffLongitude);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        Tuple tuple = null;

        if (route != null)
        {
            Tuple subTuple = TupleBuilder.tuple()
                    .put("route", route)
                    .build();

            tuple = TupleBuilder.tuple().put("pickupLatitude", pickupLatitude)
                    .put("pickupLongitude", pickupLongitude)
                    .put("dropoffLatitude", dropoffLatitude)
                    .put("dropoffLongitude", dropoffLongitude)
                    .put("pickupDatetime", pickupDatetime)
                    .put("dropoffDatetime", dropoffDatetime)
                    .put("route", route)
                    .put("newid", subTuple)
                    .build();

            System.out.println("tuple hash:" + tuple.hashCode());

        }

        return tuple;
    }

    private String generateID(LatLonPoint.Double targetPoint) throws Exception{


        double azimuth = originPoint.azimuth(targetPoint);
        double distance = Length.METER.fromRadians(originPoint.distance(targetPoint));


        double xOffset = distance * Math.sin(azimuth);
        double yOffset = distance * Math.cos(azimuth);

        java.lang.Double xDouble = Math.ceil(xOffset / EDGE);
        Integer xInt = xDouble.intValue();

        java.lang.Double yDouble = Math.ceil(Math.abs(yOffset) / EDGE);
        Integer yInt = yDouble.intValue();

        if (xInt < 0 || xInt > 300 || yInt < 0 || yInt > 300)
        {
            Exception e = new Exception("coordinates out of boundry! xInt:" + xInt + " yInt" + yInt);
            throw e;
        }

        String id = "C" + xInt + "." +yInt;

        return id;

    }

    private String generateRoute(double pickupLatitude, double pickupLongitude, double dropoffLatitude, double dropoffLongitude) throws Exception
    {

        LatLonPoint.Double toPoint = new LatLonPoint.Double(dropoffLatitude, dropoffLongitude);
        LatLonPoint.Double fromPoint = new LatLonPoint.Double(pickupLatitude, pickupLongitude);

        String fromID = null;
        String toID = null;

        try
        {
            fromID = generateID(fromPoint);
            toID = generateID(toPoint);
        }
        catch (Exception e)
        {
            throw e;
        }



        return fromID + "_" + toID;

    }

}
