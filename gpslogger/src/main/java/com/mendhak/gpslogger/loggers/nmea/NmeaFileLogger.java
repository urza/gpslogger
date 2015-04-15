/*******************************************************************************
 * This file is part of GPSLogger for Android.
 *
 * GPSLogger for Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * GPSLogger for Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GPSLogger for Android.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.mendhak.gpslogger.loggers.nmea;

import com.mendhak.gpslogger.common.AppSettings;
import com.mendhak.gpslogger.common.RejectionHandler;
import com.mendhak.gpslogger.common.Session;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NmeaFileLogger {

    protected final static Object lock = new Object();
    String fileName;
    private final static ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(128), new RejectionHandler());

    public NmeaFileLogger(String fileName) {
        this.fileName = fileName;
    }

    public void Write(long timestamp, String nmeaSentence) {

        File gpxFolder = new File(AppSettings.getGpsLoggerFolder());
        if (!gpxFolder.exists()) {
            gpxFolder.mkdirs();
        }

        File gpxFile = new File(gpxFolder.getPath(), Session.getCurrentFileName() + ".nmea");


        NmeaWriteHandler writeHandler = new NmeaWriteHandler(gpxFile, nmeaSentence);
        EXECUTOR.execute(writeHandler);
    }
}

class NmeaWriteHandler implements Runnable {

    File gpxFile;
    String nmeaSentence;

    NmeaWriteHandler(File gpxFile, String nmeaSentence) {
        this.gpxFile = gpxFile;
        this.nmeaSentence = nmeaSentence;
    }

    @Override
    public void run() {

        synchronized (NmeaFileLogger.lock) {

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(gpxFile, true));
                writer.write(nmeaSentence);
                writer.newLine();
                writer.close();

            } catch (IOException e) {

            }
        }

    }
}
