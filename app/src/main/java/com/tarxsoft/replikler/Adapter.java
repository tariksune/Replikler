package com.tarxsoft.replikler;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.muddzdev.styleabletoast.StyleableToast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> implements Filterable {

    LayoutInflater layoutInflater;
    List<Quotes> quotes;
    List<Quotes> quotesFilter;
    Context context;
    MediaPlayer mediaPlayer = new MediaPlayer();

    public Adapter(Context context,List<Quotes> quotes){
        this.layoutInflater = LayoutInflater.from(context);
        this.quotes = quotes;
        this.context = context;
        this.quotesFilter = quotes;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.quote_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.quoteText.setText(quotesFilter.get(position).getQuoteText());
        holder.quoteName.setText(quotesFilter.get(position).getQuoteName());
        holder.quoteRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(quotesFilter.get(position).getQuoteLink());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
        holder.quoteRelativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v)  {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                builder.setTitle("Ne yapmak istiyorsun?");
                //builder.setCancelable(false);
                String[] options = {"İndir", "Paylaş"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:

                                downloadQuote(position);
                                break;
                            case 1:
                                try {
                                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                                    StrictMode.setVmPolicy(builder.build());
                                    String stringFile = Environment.getExternalStorageDirectory().getPath() + File.separator + "Download/" + quotesFilter.get(position).getQuoteId() +".mp3";
                                    File file = new File(stringFile);
                                    if (!file.exists()){
                                        Log.d("tag","string:"+stringFile );
                                        StyleableToast.makeText(context, "Paylaşmak için indirmeniz gerekmektedir.", Toast.LENGTH_LONG, R.style.mytoast).show();
                                        return;
                                    }
                                    Intent shareFile = new Intent(Intent.ACTION_SEND);
                                    shareFile.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    shareFile.setType("*/*");
                                    shareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+file));
                                    v.getContext().startActivity(shareFile);

                                } catch (Exception e) {
                                    Log.e(">>>", "Error: " + e);
                                }
                                break;
                            default:

                        }
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return quotesFilter.size();
    }

    @Override
    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint.toString().isEmpty()){
                    quotesFilter = quotes;
                }else{
                    List<Quotes> quoteFilterList = new ArrayList<>();
                    for (Quotes quotes: quotes){
                        if (quotes.getQuoteName().toLowerCase().contains(constraint.toString().toLowerCase())){
                            quoteFilterList.add(quotes);
                        }
                    }
                    quotesFilter = quoteFilterList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = quotesFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                quotesFilter = (ArrayList<Quotes>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView quoteText,quoteName,quoteLink;
        RelativeLayout quoteRelativeLayout;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            quoteText = itemView.findViewById(R.id.quoteText);
            quoteName = itemView.findViewById(R.id.quoteName);
            quoteLink = itemView.findViewById(R.id.quoteLink);
            quoteRelativeLayout = itemView.findViewById(R.id.quoteRelativeLayout);
        }
    }

    private void downloadQuote(final int position){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(quotesFilter.get(position).getQuoteLink()));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, quotesFilter.get(position).getQuoteId() +".mp3");
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
        return;
    }


}
