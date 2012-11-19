
package net.myfigurecollection.android;

import java.io.IOException;

import net.myfigurecollection.android.webservices.MFCService;
import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataOutput;
import android.app.backup.SharedPreferencesBackupHelper;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class MFCBackup extends BackupAgentHelper
{
	static final String	PREFS				= MFCService.PREFERENCES;

	static final String	PREFS_BACKUP_KEY	= "shared_preferences";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.app.backup.BackupAgentHelper#onBackup(android.os.ParcelFileDescriptor
	 * , android.app.backup.BackupDataOutput, android.os.ParcelFileDescriptor)
	 */
	@Override
	public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException
	{
		Log.d("MFC Backup", "Baking up datas : " + data);
		super.onBackup(oldState, data, newState);

	}

	@Override
	public void onCreate()
	{
		Log.d("MFC Backup", "Creating back up agent");
		super.onCreate();
		SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, MFCBackup.PREFS);
		addHelper(MFCBackup.PREFS_BACKUP_KEY, helper);
	}
}
