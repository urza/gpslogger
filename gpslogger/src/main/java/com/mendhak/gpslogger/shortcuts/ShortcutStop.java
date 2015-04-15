/*
*    This file is part of GPSLogger for Android.
*
*    GPSLogger for Android is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 2 of the License, or
*    (at your option) any later version.
*
*    GPSLogger for Android is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with GPSLogger for Android.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.mendhak.gpslogger.shortcuts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.mendhak.gpslogger.GpsLoggingService;
import com.mendhak.gpslogger.common.events.CommandEvents;
import de.greenrobot.event.EventBus;
import org.slf4j.LoggerFactory;

public class ShortcutStop extends Activity {

    private static final org.slf4j.Logger tracer = LoggerFactory.getLogger(ShortcutStop.class.getSimpleName());

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        tracer.info("Shortcut - stop logging");
        EventBus.getDefault().postSticky(new CommandEvents.RequestStartStop(false));

        Intent serviceIntent = new Intent(getApplicationContext(), GpsLoggingService.class);
        getApplicationContext().startService(serviceIntent);

        finish();

    }


}