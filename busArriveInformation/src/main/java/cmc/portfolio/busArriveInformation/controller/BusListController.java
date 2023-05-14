package cmc.portfolio.busArriveInformation.controller;

import cmc.portfolio.busArriveInformation.process.Bus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
public class BusListController {
//    @GetMapping("busLists")
    @RequestMapping(value = "/busList", method = RequestMethod.POST)
    public ModelAndView busLists(ModelAndView mav, HttpServletRequest request) throws IOException {
        Bus busInformation = new Bus();

        mav.addObject("busLists", busInformation.getBusListById(Integer.valueOf(request.getParameter("busNum"))));
        mav.setViewName("busList");
        return mav;
    }
}
