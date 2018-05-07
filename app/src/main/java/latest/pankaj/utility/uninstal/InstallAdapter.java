package latest.pankaj.utility.uninstal;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import latest.pankaj.utility.R;

/**
 * Created by Pankaj on 27/10/2017.
 */

public class InstallAdapter extends RecyclerView.Adapter<InstallAdapter.InstallHolder> {

    Context mContext;
    List<PackageInfo> pkgAppsList;
    PackageManager packageManager;
    public InstallAdapter(Context mContext, List<PackageInfo> pkgAppsList, PackageManager packageManager) {
        this.mContext=mContext;
        this.pkgAppsList=pkgAppsList;
        this.packageManager=packageManager;


    }

    @Override
    public InstallHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_view_install,parent,false);
        return new InstallHolder(view);
    }

    @Override
    public void onBindViewHolder(InstallHolder holder, int position) {
        PackageInfo packageInfo = (PackageInfo) getItem(position);

        Drawable appIcon = packageManager
                .getApplicationIcon(packageInfo.applicationInfo);
        String appName = packageManager.getApplicationLabel(
                packageInfo.applicationInfo).toString();

        holder.tvTextView.setText(appName);
    }

    @Override
    public int getItemCount() {
        return pkgAppsList.size();
    }

    public Object getItem(int position) {
        return pkgAppsList.get(position);
    }
    public class InstallHolder extends RecyclerView.ViewHolder{

        TextView tvTextView;
        public InstallHolder(View itemView) {
            super(itemView);
            tvTextView=itemView.findViewById(R.id.tvTextView);
        }
    }
}
