//
//  ViewController.swift
//  Zone
//
//  Created by Heverton Rodrigues on 11/11/14.
//  Copyright (c) 2014 Heverton Rodrigues. All rights reserved.
//

import UIKit

class ViewController: UIViewController
{
    var mainLoader :HRLoader?

    override func viewDidLoad()
    {
        super.viewDidLoad()
        
        mainLoader = HRLoader(frame: self.view.frame)
        mainLoader?.isMain()
        self.view.addSubview(mainLoader!)
        
        self.navigationController?.navigationBarHidden = true
        
        NSTimer.scheduledTimerWithTimeInterval(3, target: self, selector: "openApp", userInfo: nil, repeats: false)
    }
    
    
    @IBAction func getLocation(sender: UIButton)
    {
        Preferences.sharedInstance.getDeviceLocation().latitude
        Preferences.sharedInstance.getDeviceLocation().longitude
    }
    
    func openApp()
    {
        self.performSegueWithIdentifier( "openApp", sender: self)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


}

