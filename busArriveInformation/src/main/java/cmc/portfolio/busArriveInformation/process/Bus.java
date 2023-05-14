package cmc.portfolio.busArriveInformation.process;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.Connection;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import javax.imageio.ImageTranscoder;
import java.util.Vector;

public class Bus{

    public Map<String, String> getBusInformation(int routeId) throws IOException {
        Map<String, String> dict = new HashMap<String,String>();
        Connection connDeltail = Jsoup.connect("http://openapi.gbis.go.kr/ws/rest/busrouteservice/info?serviceKey=1234567890&routeId=" + routeId);
        Document documentDeltail = connDeltail.get();
        Elements busRouteInfoItems = documentDeltail.select("busRouteInfoItem");

        for (Element busRouteInfoItem : busRouteInfoItems) {
            dict.put("downFirstTime", busRouteInfoItem.select("downFirstTime").text());
            dict.put("downLastTime", busRouteInfoItem.select("downLastTime").text()); // 평일종점 첫차 출발시간
            dict.put("startStationName", busRouteInfoItem.select("startStationName").text()); // 기점정류소명칭
            dict.put("endStationName", busRouteInfoItem.select("endStationName").text()); //종점정류소명칭
            dict.put("peekAlloc", busRouteInfoItem.select("peekAlloc").text()); // 평일최소 배차시간(분)
            dict.put("BusNum", busRouteInfoItem.select("routeName").text()); // 버스 번호
            dict.put("routeTypeName", busRouteInfoItem.select("routeTypeName").text());
        }

        return dict;
    }

    public Vector<Map<String, String>> getBusListById(int busNUm) throws IOException {
        Connection conn = Jsoup.connect("http://openapi.gbis.go.kr/ws/rest/busrouteservice?serviceKey=1234567890&keyword=" + busNUm);
        Document document = conn.get();
        Elements busLists = document.select("busRouteList");
        Map<String, String> dict ;

        Vector<Map<String, String>> lists = new Vector<>();

        for(Element tag : busLists) {
            dict = new HashMap<String,String>();

            dict.put("regionName", tag.select("regionName").text()); // 운행 지역
            dict.put("routeid", tag.select("routeid").text()); // 노선 관리 번호

            Connection connDeltail = Jsoup.connect("http://openapi.gbis.go.kr/ws/rest/busrouteservice/info?serviceKey=1234567890&routeId=" + tag.select("routeid").text());
            Document documentDeltail = connDeltail.get();
            Elements busRouteInfoItems = documentDeltail.select("busRouteInfoItem");

            Map<String, String> detailInformation = getBusInformation(Integer.valueOf(tag.select("routeid").text()));

            for (String key : detailInformation.keySet()) {
                dict.put(key, detailInformation.get(key));
            }
            lists.add(dict);
        }

        return lists;
    }


    public Vector<Map<String, String>> getRouteStationList(int routeId) throws IOException {
        Connection conn = Jsoup.connect("http://openapi.gbis.go.kr/ws/rest/busrouteservice/station?serviceKey=1234567890&routeId=" + routeId);
        Document document = conn.get();
        Elements routeLists = document.select("busRouteStationList");
        Vector<Map<String, String>> lists = new Vector<>();

        for (Element routeList : routeLists) {
            Map<String, String> dict = new HashMap<String,String>();

            dict.put("stationName", routeList.select("stationName").text());
            dict.put("stationId", routeList.select("stationId").text());
            dict.put("turnYn", routeList.select("turnYn").text());

            lists.add(dict);
        }

        return lists;
    }

    public Vector<String> getBusLocationList(int routeId) throws IOException {
        Connection conn = Jsoup.connect("http://openapi.gbis.go.kr/ws/rest/buslocationservice?serviceKey=1234567890&routeId=" + routeId);
        Document document = conn.get();
        Elements locationLists = document.select("busLocationList");

        Vector<String> lists = new Vector<>();

        for (Element busLocation : locationLists) {
            lists.add(busLocation.select("stationId").text());
        }

        return lists;
    }
    public static void main(String[] args) throws IOException {
        Bus getBusInformation = new Bus();

        //System.out.println(getBusInformation.getBusListById(320));

        for(Map<String, String> a : getBusInformation.getBusListById(320)) {
            System.out.println(a);
        }

//        getBusInformation.getRouteStationList(234001621);
        for(Map<String, String> a : getBusInformation.getRouteStationList(234001621)) {
             System.out.println(a);
        }

        for(String a : getBusInformation.getBusLocationList(234001621)) {
            System.out.println(a);
        }
    }
}
