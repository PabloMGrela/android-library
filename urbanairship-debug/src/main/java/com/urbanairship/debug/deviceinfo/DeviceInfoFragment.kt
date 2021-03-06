/* Copyright Urban Airship and Contributors */

package com.urbanairship.debug.deviceinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.urbanairship.debug.R
import com.urbanairship.debug.deviceinfo.preferences.InAppAutomationDisplayIntervalPreference
import com.urbanairship.debug.deviceinfo.preferences.InAppAutomationDisplayIntervalPreferenceDialogFragment
import com.urbanairship.debug.extensions.setupToolbarWithNavController

/**
 * Settings fragment.
 *
 * Wraps the PreferenceFragment.
 */
class DeviceInfoFragment : androidx.fragment.app.Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.ua_fragment_device_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                    .replace(R.id.preference_placeholder, PreferenceFragment())
                    .commitNow()
        }

        setupToolbarWithNavController(R.id.toolbar)
    }

    /**
     * PreferenceFragmentCompat
     */
    class PreferenceFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(bundle: Bundle?, s: String?) {
            addPreferencesFromResource(R.xml.ua_device_info)
        }

        override fun onPreferenceTreeClick(preference: androidx.preference.Preference): Boolean {
            val view = view
            if (view != null && TAGS_KEY == preference.key) {
                Navigation.findNavController(view).navigate(R.id.deviceInfoTagsFragment)
            }

            if (view != null && ATTRIBUTES_KEY == preference.key) {
                Navigation.findNavController(view).navigate(R.id.deviceInfoAttributesFragment)
            }

            return super.onPreferenceTreeClick(preference)
        }

        override fun onDisplayPreferenceDialog(preference: Preference?) {
            if(preference is InAppAutomationDisplayIntervalPreference) {
                val dialogFragment = InAppAutomationDisplayIntervalPreferenceDialogFragment.newInstance(preference.key)
                if(dialogFragment != null) {
                    dialogFragment.setTargetFragment(this, 0)
                    fragmentManager?.let { dialogFragment.show(it, DISPLAY_INTERVAL_TAG) }
                } else {
                    super.onDisplayPreferenceDialog(preference)
                }
            } else {
                super.onDisplayPreferenceDialog(preference)
            }
        }

        companion object {
            private val TAGS_KEY = "tags"
            private val DISPLAY_INTERVAL_TAG = "DISPLAY_INTERVAL_TAG"
            private val ATTRIBUTES_KEY = "attributes"
        }

    }
}