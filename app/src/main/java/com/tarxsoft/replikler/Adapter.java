package com.tarxsoft.replikler;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    LayoutInflater layoutInflater;
    List<Quote> quotes;
    Context context;


    public Adapter(Context context,List<Quote> quotes){
        this.layoutInflater = LayoutInflater.from(context);
        this.quotes = quotes;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.quote_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.quoteText.setText(quotes.get(position).getQuoteText());
        holder.quoteName.setText(quotes.get(position).getQuoteName());
        holder.quoteRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mediaPlayer = new MediaPlayer();
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                try {
                    mediaPlayer.setDataSource(quotes.get(position).getQuoteLink());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        holder.quoteRelativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v)  {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                builder.setTitle("Ne yapmak istiyorsun?");

                String[] options = {"İndir", "Paylaş"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(quotes.get(position).getQuoteLink()));
                                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                request.setTitle(quotes.get(position).getQuoteText());
                                request.setDescription(quotes.get(position).getQuoteName());
                                request.allowScanningByMediaScanner();
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,""+ System.currentTimeMillis());
                                DownloadManager manager = (DownloadManager) v.getRootView().getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                                manager.enqueue(request);
                            case 1:
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
        return quotes.size();
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

    private void beginDownload(){


    }
}
