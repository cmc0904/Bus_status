package cmc.portfolio.busArriveInformation.controller;

import cmc.portfolio.busArriveInformation.process.Bus;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.sql.Statement;
import java.util.Map;
import java.util.Vector;

@Controller
public class BusRouteController {
    @GetMapping("busRoute")
    public String busRoute(Model model, @RequestParam(value="routeid") String routeid, @RequestParam(value="busNum") String busNum) throws IOException {

        Bus busInformation = new Bus();
        Vector<String[]> lists = new Vector<>();


        Vector<String> busLocation = busInformation.getBusLocationList(Integer.valueOf(routeid));
        for(Map<String, String> station : busInformation.getRouteStationList(Integer.valueOf(routeid))) {
            String a = busLocation.contains(station.get("stationId")) == true? "Y" : "N";
            if (station.get("turnYn").equals("N")) {
                lists.add(new String[] {station.get("stationName"), station.get("stationId"), a});
            } else {
                model.addAttribute("first", lists);
                model.addAttribute("turnYn", new String[] {station.get("stationName"), station.get("stationId"), a});
                lists = new Vector<>();
            }

        }

        Map<String, String> detailInformation = busInformation.getBusInformation(Integer.valueOf(routeid));
        model.addAttribute("routeTypeName", detailInformation.get("routeTypeName"));
        model.addAttribute("startStationName", detailInformation.get("startStationName"));
        model.addAttribute("endStationName", detailInformation.get("endStationName"));
        model.addAttribute("second", lists);
        model.addAttribute("busNum", busNum);

        return "busRoute";
    }
}
