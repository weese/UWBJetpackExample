/*
 * Copyright 2022 NXP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jetpackexample;

import com.jetpackexample.utils.Utils;

import java.io.Serializable;

// Reverse-engineered config from the Estimote UWB app
// all values in little-endian
//
// short specVerMajor // must be 1
// short specVerMinor // must be 1
// 4: 0x19 0x45 0x55 // ??
// 7:  int   Session_ID
// 11: byte  Preamble_Code
// 12: byte  Channel_Number
// 13: short Number_of_slots // Round_Duration_RSTU = Number_of_slots * Slot_Duration_RSTU
// 15: short Slot_Duration_RSTU
// 17: short Block_Duration_ms
// 19: byte  unknown // must be 3, so that ToF_Report = 1
// 20-25: byte Static_STS_IV[6] // in reverse order
// 26: byte DST_ADDR[2]
// 28: short Block_Timing_Stability

// Vendor_ID[2] = { 0x4C, 0x00 };
// role = 1; // gCurrentRangingRole
// STS_Config = 0;
// MAX_RR_Retry = 0;
// enc_payload = 1;
// Ranging_Round_Usage = 2;
// SP0_PHY_Set = 2;
// SP3_PHY_Set = 4;
// Rframe_Config = 3;
// UWB_Init_Time_ms = 5;

public class UwbPhoneConfigData implements Serializable {
    public short specVerMajor;
    public short specVerMinor;
    public byte[] reserved;
    public int sessionId;
    public byte preambleId;
    public byte channel;
    public short numberOfSlots;
    public short slotDurationRSTU;
    public short blockDurationMs;
    public byte unknown;
    public byte[] staticSTSIV;
    public byte[] phoneMacAddress;
    public short blockTimingStability;
 
    public UwbPhoneConfigData() {
        /**
         * Pre-defined one-to-many STATIC STS DS-TWR ranging
         *
         * deferred mode,
         * ranging interval = 200 ms,
         * slot duration = 2400 RSTU,
         * slots per ranging round = 20
         *
         * All other MAC parameters use FiRa/UCI default values.
         *
         * <p> Typical use case: smart phone interacts with many smart devices
         */
        
        specVerMajor = 1;
        specVerMinor = 1;
        reserved = new byte[]{0x19, 0x45, 0x55};
        unknown = 3;
        preambleId = 11;
        channel = 9;
//        numberOfSlots = 6;
//        slotDurationRSTU = 3600;
//        blockDurationMs = 180;
        slotDurationRSTU = 2400;
        blockDurationMs = 200;
        numberOfSlots = 20;
        staticSTSIV = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        phoneMacAddress = new byte[]{0x4c, 0x00};
        blockTimingStability = 0x0064;
    }


    public byte[] toByteArray() {
        byte[] response = null;
        response = Utils.concat(response, Utils.shortToByteArray(this.specVerMajor));
        response = Utils.concat(response, Utils.shortToByteArray(this.specVerMinor));
        response = Utils.concat(response, this.reserved);
        response = Utils.concat(response, Utils.intToByteArray(this.sessionId));
        response = Utils.concat(response, Utils.byteToByteArray(this.preambleId));
        response = Utils.concat(response, Utils.byteToByteArray(this.channel));
        response = Utils.concat(response, Utils.shortToByteArray(this.numberOfSlots));
        response = Utils.concat(response, Utils.shortToByteArray(this.slotDurationRSTU));
        response = Utils.concat(response, Utils.shortToByteArray(this.blockDurationMs));
        response = Utils.concat(response, Utils.byteToByteArray(this.unknown));
        response = Utils.concat(response, this.staticSTSIV);
        response = Utils.concat(response, this.phoneMacAddress);
        response = Utils.concat(response, Utils.shortToByteArray(this.blockTimingStability));

        return response;
    }

    public static UwbPhoneConfigData fromByteArray(byte[] data) {
        UwbPhoneConfigData uwbPhoneConfigData = new UwbPhoneConfigData();
        uwbPhoneConfigData.specVerMajor = Utils.byteArrayToShort(Utils.extract(data, 2, 0));
        uwbPhoneConfigData.specVerMinor = Utils.byteArrayToShort(Utils.extract(data, 2, 2));
        uwbPhoneConfigData.reserved = Utils.extract(data, 3, 4);
        uwbPhoneConfigData.sessionId = Utils.byteArrayToInt(Utils.extract(data, 4, 7));
        uwbPhoneConfigData.preambleId = Utils.byteArrayToByte(Utils.extract(data, 1, 11));
        uwbPhoneConfigData.channel = Utils.byteArrayToByte(Utils.extract(data, 1, 12));
        uwbPhoneConfigData.numberOfSlots = Utils.byteArrayToShort(Utils.extract(data, 2, 13));
        uwbPhoneConfigData.slotDurationRSTU = Utils.byteArrayToShort(Utils.extract(data, 2, 15));
        uwbPhoneConfigData.blockDurationMs = Utils.byteArrayToShort(Utils.extract(data, 2, 17));
        uwbPhoneConfigData.unknown = Utils.byteArrayToByte(Utils.extract(data, 1, 19));
        uwbPhoneConfigData.staticSTSIV = Utils.extract(data, 6, 20);
        uwbPhoneConfigData.phoneMacAddress = Utils.extract(data, 2, 26);
        uwbPhoneConfigData.blockTimingStability = Utils.byteArrayToShort(Utils.extract(data, 2, 28));

        return uwbPhoneConfigData;
    }
}
