package sg.edu.rp.joelum.sapAssignment;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

public class mpWidget extends AppWidgetProvider {
	
	public static final String UPDATEALERT = "edu.rp.sdwidget.APPWIDGET_UPDATE";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
		if(UPDATEALERT.equals(intent.getAction())) {
			Bundle b = intent.getExtras();
			
			String status = b.getString("status");
			
			ComponentName thisWidget = new ComponentName(context,mpWidget.class);
			
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			
			int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
			
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.mp_widget_view);
			
			views.setTextViewText(R.id.disp, status);
			
			appWidgetManager.updateAppWidget(appWidgetIds, views);
			
			Log.d("onReceiveString", "-> " + status);
		}
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		Intent randomServiceIntent = new Intent(context, widgetService.class);
		context.startService(randomServiceIntent);
		
		int appWidgetId = appWidgetIds[0];
		
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.mp_widget_view);
		
		String string = "[Loading..]";
		
		views.setTextViewText(R.id.disp, string);
		
		appWidgetManager.updateAppWidget(appWidgetId, views);
		
		Log.d("onUpdateString", "-> " + string);
	}
}
