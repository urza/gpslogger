/*
*    This file is part of GPSLogger for Android.
*
*    GPSLogger for Android is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
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

package com.mendhak.gpslogger.senders.ftp;

import android.os.Bundle;
import android.preference.Preference;
import com.afollestad.materialdialogs.prefs.MaterialEditTextPreference;
import com.afollestad.materialdialogs.prefs.MaterialListPreference;
import com.mendhak.gpslogger.R;
import com.mendhak.gpslogger.common.EventBusHook;
import com.mendhak.gpslogger.common.PreferenceValidationFragment;
import com.mendhak.gpslogger.common.Utilities;
import com.mendhak.gpslogger.common.events.UploadEvents;
import com.mendhak.gpslogger.views.component.CustomSwitchPreference;
import de.greenrobot.event.EventBus;
import org.slf4j.LoggerFactory;

public class AutoFtpFragment
        extends PreferenceValidationFragment implements Preference.OnPreferenceClickListener {
    private static final org.slf4j.Logger tracer = LoggerFactory.getLogger(AutoFtpFragment.class.getSimpleName());

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.autoftpsettings);

        Preference testFtp = findPreference("autoftp_test");
        testFtp.setOnPreferenceClickListener(this);
        RegisterEventBus();
    }

    @Override
    public void onDestroy() {

        UnregisterEventBus();
        super.onDestroy();
    }

    private void RegisterEventBus() {
        EventBus.getDefault().register(this);
    }

    private void UnregisterEventBus(){
        try {
            EventBus.getDefault().unregister(this);
        } catch (Throwable t){
            //this may crash if registration did not go through. just be safe
        }
    }

    private boolean IsFormValid() {

        CustomSwitchPreference chkEnabled = (CustomSwitchPreference) findPreference("autoftp_enabled");
        MaterialEditTextPreference txtServer = (MaterialEditTextPreference) findPreference("autoftp_server");
        MaterialEditTextPreference txtUserName = (MaterialEditTextPreference) findPreference("autoftp_username");
        MaterialEditTextPreference txtPort = (MaterialEditTextPreference) findPreference("autoftp_port");


        return !chkEnabled.isChecked() || txtServer.getText() != null
                && txtServer.getText().length() > 0 && txtUserName.getText() != null
                && txtUserName.getText().length() > 0 && txtPort.getText() != null
                && txtPort.getText().length() > 0;

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        FtpHelper helper = new FtpHelper();

        MaterialEditTextPreference servernamePreference = (MaterialEditTextPreference) findPreference("autoftp_server");
        MaterialEditTextPreference usernamePreference = (MaterialEditTextPreference) findPreference("autoftp_username");
        MaterialEditTextPreference passwordPreference = (MaterialEditTextPreference) findPreference("autoftp_password");
        MaterialEditTextPreference portPreference = (MaterialEditTextPreference) findPreference("autoftp_port");
        CustomSwitchPreference useFtpsPreference = (CustomSwitchPreference) findPreference("autoftp_useftps");
        MaterialListPreference sslTlsPreference = (MaterialListPreference) findPreference("autoftp_ssltls");
        CustomSwitchPreference implicitPreference = (CustomSwitchPreference) findPreference("autoftp_implicit");
        MaterialEditTextPreference directoryPreference = (MaterialEditTextPreference) findPreference("autoftp_directory");

        if (!helper.ValidSettings(servernamePreference.getText(), usernamePreference.getText(), passwordPreference.getText(),
                Integer.valueOf(portPreference.getText()), useFtpsPreference.isChecked(), sslTlsPreference.getValue(),
                implicitPreference.isChecked())) {
            Utilities.MsgBox(getString(R.string.autoftp_invalid_settings),
                    getString(R.string.autoftp_invalid_summary),
                    getActivity());
            return false;
        }

        Utilities.ShowProgress(getActivity(), getString(R.string.autoftp_testing),
                getString(R.string.please_wait));


        helper.TestFtp(servernamePreference.getText(), usernamePreference.getText(), passwordPreference.getText(),
                directoryPreference.getText(), Integer.valueOf(portPreference.getText()), useFtpsPreference.isChecked(),
                sslTlsPreference.getValue(), implicitPreference.isChecked());

        return true;
    }


    @Override
    public boolean IsValid() {
        return IsFormValid();
    }

    @EventBusHook
    public void onEventMainThread(UploadEvents.Ftp o){
        tracer.debug("FTP Event completed, success: " + o.success);
        Utilities.HideProgress();
        if(!o.success){
            Utilities.MsgBox(getString(R.string.sorry), "FTP Test Failed", getActivity());
        }
        else {
            Utilities.MsgBox(getString(R.string.success), "FTP Test Succeeded", getActivity());
        }
    }
}