from flask import Flask, redirect, url_for, request, render_template

import requests
from bs4 import BeautifulSoup
app = Flask(__name__)
app.config['JSON_AS_ASCII'] = False

def getBuslocation(routeid):
    response = requests.get(
        'http://openapi.gbis.go.kr/ws/rest/buslocationservice?serviceKey=1234567890&routeId=%s' % routeid)

    soup = BeautifulSoup(response.text, 'html.parser')

    return soup.select('busLocationList')

def getBusRoute(routeid):
    all_station = list()
    response = requests.get(
        'http://openapi.gbis.go.kr/ws/rest/busrouteservice/station?serviceKey=1234567890&routeId=%s' % routeid)

    soup = BeautifulSoup(response.text, 'html.parser')

    for i in soup.select('busRouteStationList'):
        all_station.append([i.select('stationName')[0].text, i.select('stationId')[0].text, i.select('turnYn')[0].text])
    return all_station


@app.route('/')
def main():
    return render_template('busSearch.html')

@app.route("/busList", methods = ['POST'])
def hello():
    if request.method == 'POST':
        data = list()
        busNum = request.form['myName']
        print(busNum)
        response = requests.get('http://openapi.gbis.go.kr/ws/rest/busrouteservice?serviceKey=1234567890&keyword=%s' % busNum)

        soup = BeautifulSoup(response.text, 'html.parser')

        for tag in soup.select('busRouteList'):
            information_route = dict()
            information_route['regionName'] = tag.select('regionName')[0].text
            information_route['routeid'] = tag.select('routeid')[0].text
            information_route['regionName'] = tag.select('regionName')[0].text

            response = requests.get('http://openapi.gbis.go.kr/ws/rest/busrouteservice/info?serviceKey=1234567890&routeId=%s' % tag.select('routeId')[0].text)
            about_route = BeautifulSoup(response.text, 'html.parser')

            for info in about_route.select('busRouteInfoItem'):
                try:
                    information_route['downFirstTime'] = info.select('downFirstTime')[0].text #평일종점 첫차 출발시간
                    information_route['downLastTime'] = info.select('downLastTime')[0].text # 평일종점 첫차 출발시간
                    information_route['startStationName'] = info.select('startStationName')[0].text # 기점정류소명칭
                    information_route['endStationName'] = info.select('endStationName')[0].text #종점정류소명칭
                    information_route['peekAlloc'] = info.select('peekAlloc')[0].text # 평일최소 배차시간(분)
                    information_route['BusNum'] = info.select('routeName')[0].text # 버스 번호
                except:
                    pass
                if tag.select('routeid')[0].text:
                    data.append(information_route)

    return render_template('busLists.html', values=data)


@app.route("/<routeid>/<busNum>")
def hello1(routeid, busNum):
    all_route = getBusRoute(routeid)
    print(all_route)

    bus_location = getBuslocation(routeid)
    bus_location_info = list()
    all_data = {}

    for location in bus_location:
        for route in all_route:
            if route[1] == location.select('stationId')[0].text:
                bus_location_info.append(route[1])
    route = list()
    for i in all_route:
        if i[2] == "N":
            route.append(i[:2])
        elif i[2] == "Y":
            all_data['fist_route'] = route
            all_data['turnYn'] = [i[:2]]
            route = list()

    all_data['second_route'] = route
    all_data['location_list'] = bus_location_info
    all_data['busNum'] = busNum

    response = requests.get('http://openapi.gbis.go.kr/ws/rest/busrouteservice/info?serviceKey=1234567890&routeId=%s' %
                            routeid)
    about_route = BeautifulSoup(response.text, 'html.parser')

    all_data['startStationName'] = about_route.select('startStationName')[0].text  # 기점정류소명칭
    all_data['endStationName'] = about_route.select('endStationName')[0].text  # 종점정류소명칭
    all_data['routeTypeName'] = about_route.select('routeTypeName')[0].text  # 종점정류소명칭

    return render_template('busRoute.html', values=all_data)


if __name__ == "__main__":
    app.run(host="0.0.0.0", port="8080", debug=True)



