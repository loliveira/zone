//
//  Preferences.swift
//  BovControl
//
//  Created by Heverton Rodrigues on 16/09/14.
//  Copyright (c) 2014 Heverton Rodrigues. All rights reserved.
//

import UIKit
import Foundation
import CoreLocation

let COLOR_1 :String = ""
private let baseURL :String = "https://"


class Preferences
{
    let locationManager :CLLocationManager = CLLocationManager()
    
    class func API( endPoint :String ) -> String
    {
        let action :String = endPoint.stringByReplacingOccurrencesOfString("/", withString: "", options: nil, range: nil)
        return "\(baseURL)/\(action)"
    }
    
    func getDeviceLocation() -> ( latitude :Double, longitude :Double )
    {
        // #TODO : STOP CRASHS
        var lat :Double = 0
        var lng :Double = 0
        
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.distanceFilter = kCLDistanceFilterNone
        
        if ( HRSkill.ios8() ) {
            locationManager.requestAlwaysAuthorization()
        }
        locationManager.startUpdatingLocation()
        
//        if ( locationManager.location.coordinate.latitude.isNormal )
//        {
            lat = locationManager.location.coordinate.latitude
//        }
//        
//        if ( locationManager.location.coordinate.longitude.isNormal )
//        {
            lng = locationManager.location.coordinate.longitude
//        }
        
        return ( lat, lng )
    }
    
    
    
    
    class var sharedInstance: Preferences
    {
        struct Static
        {
            static var instance: Preferences?
            static var token: dispatch_once_t = 0
        }
        
        dispatch_once(&Static.token)
        {
            Static.instance = Preferences()
        }
        
        return Static.instance!
    }
}