package su.ias.utils.navigationutils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created on 6/1/17.
 */

class NavProgramAdapter extends BaseAdapter {

    private List<ApplicationInfo> data;

    public NavProgramAdapter() {
        this.data = NavigatorHelper.getNavigatorProgramList();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_navigator, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ApplicationInfo info = (ApplicationInfo) getItem(position);
        PackageManager packageManager = parent.getContext().getPackageManager();
        holder.icon.setImageDrawable(info.loadIcon(packageManager));
        holder.title.setText(info.loadLabel(packageManager));

        return convertView;
    }

    private static class ViewHolder {
        final TextView title;
        final ImageView icon;

        ViewHolder(View itemView) {
            title = (TextView) itemView.findViewById(R.id.tv_title);
            icon = (ImageView) itemView.findViewById(R.id.iv_icon);
        }
    }
}
