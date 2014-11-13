//
//  Extensions.swift
//  BovControl
//
//  Created by Heverton Rodrigues on 16/09/14.
//  Copyright (c) 2014 Heverton Rodrigues. All rights reserved.
//
import Foundation
import UIKit

extension String  {
//    var md5: String! {
//        let str = self.cStringUsingEncoding(NSUTF8StringEncoding)
//            let strLen = CC_LONG(self.lengthOfBytesUsingEncoding(NSUTF8StringEncoding))
//            let digestLen = Int(CC_MD5_DIGEST_LENGTH)
//            let result = UnsafeMutablePointer<CUnsignedChar>.alloc(digestLen)
//            
//            CC_MD5(str!, strLen, result)
//            
//            var hash = NSMutableString()
//            for i in 0..<digestLen {
//                hash.appendFormat("%02x", result[i])
//            }
//            
//            result.destroy()
//            
//            return String(format: hash)
//    }
    
    // http://stackoverflow.com/questions/6644004/how-to-check-if-nsstring-is-contains-a-numeric-value
    var isNumeric :Bool {
        return (self as NSString).rangeOfCharacterFromSet(NSCharacterSet.decimalDigitCharacterSet().invertedSet).location == NSNotFound
    }
    
    var isEmail :Bool{
        return true
    }
    
    var length :Int {
        return countElements( self )
    }
    
    func numbers( decimal :Bool = false ) -> String
    {
        var result :String = String()
        var accepted  :String = "1234567890"
        
        if decimal
        {
            accepted += "."
        }
        
        for n in self
        {
            if let accept = accepted.rangeOfString( String( n ) )?.startIndex
            {
                result += String(n)
            }
        }
        return result
    }

    func removeUnknownChars() -> String
    {
        var result :String = String()

        let accepted  :String = "abcdefghijklmnopqrstuvxywzABCDEFHGIJKLMNOPQRSTUVCYWZ1234567890ÄÅÁÂÀÃäáâàãÉÊËÈéêëèÍÎÏÌíîïìÖÓÔÒÕöóôòõÜÚÛüúûùÇçÑñ "
        
        for letter in self
        {
            if let accept = accepted.rangeOfString( String( letter ) )?.startIndex
            {
                result += String(letter)
            }
        }
        return result
    }

    
    
    func slugfy( replace :String = "-" ) -> String
    {
        var result :String = String()

        let accents   :String = "ÄÅÁÂÀÃäáâàãÉÊËÈéêëèÍÎÏÌíîïìÖÓÔÒÕöóôòõÜÚÛüúûùÇçÑñ"
        let noaccents :String = "aaaaaaaaaaaeeeeeeeeiiiiiiiioooooooooouuuuuuuccnn"
        let accepted  :String = "abcdefghijklmnopqrstuvxywz1234567890"

        for letter in self.lowercaseString
        {
            if let accept = accepted.rangeOfString( String( letter ) )?.startIndex
            {
                result += String(letter)
            } else if let pos = accents.rangeOfString( String( letter ) )?.startIndex
            {
                result += String(noaccents[pos])
            } else if letter == " " {
                result += replace
            }
        }
        return result
    }
    
    func trim() -> String {
        return self.stringByTrimmingCharactersInSet(.whitespaceAndNewlineCharacterSet())
    }
    
    func substring(location:Int, length:Int) -> String! {
        return (self as NSString).substringWithRange(NSMakeRange(location, length))
    }
    
    func location(other: String) -> Int {
        return (self as NSString).rangeOfString(other).location
    }
    
    func contains(other: String) -> Bool {
        return (self as NSString).containsString(other)
    }
    
    subscript(index: Int) -> String! {
        get {
            return self.substring(index, length: 1)
        }
    }
}


extension UIColor {
    convenience init(rgba: String) {
        var red: CGFloat   = 0.0
        var green: CGFloat = 0.0
        var blue: CGFloat  = 0.0
        var alpha: CGFloat = 1.0
        
        if rgba.hasPrefix("#") {
            let index = advance(rgba.startIndex, 1)
            let hex = rgba.substringFromIndex(index)
            let scanner = NSScanner(string: hex)
            var hexValue: CUnsignedLongLong = 0
            if scanner.scanHexLongLong(&hexValue) {
                if hex.length == 6 {
                    red   = CGFloat((hexValue & 0xFF0000) >> 16) / 255.0
                    green = CGFloat((hexValue & 0x00FF00) >> 8)  / 255.0
                    blue  = CGFloat(hexValue & 0x0000FF) / 255.0
                } else if hex.length == 8 {
                    red   = CGFloat((hexValue & 0xFF000000) >> 24) / 255.0
                    green = CGFloat((hexValue & 0x00FF0000) >> 16) / 255.0
                    blue  = CGFloat((hexValue & 0x0000FF00) >> 8)  / 255.0
                    alpha = CGFloat(hexValue & 0x000000FF)         / 255.0
                } else {
                    print("invalid rgb string, length should be 7 or 9")
                }
            } else {
                println("scan hex error")
            }
        } else {
            print("invalid rgb string, missing '#' as prefix")
        }
        self.init(red:red, green:green, blue:blue, alpha:alpha)
    }
}