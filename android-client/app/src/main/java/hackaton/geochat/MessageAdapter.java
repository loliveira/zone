package hackaton.geochat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Igor1201 on 01/11/14.
 */

public class MessageAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<Message> items = new ArrayList<Message>();

    public MessageAdapter(Context context, List items) {
        mInflater = LayoutInflater.from(context);
        this.items = items;
    }

    public int getCount() {
        return items.size();
    }

    public Message getItem(int position) {
        return items.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Message m = items.get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.chat_row, null);
            holder = new ViewHolder();
            holder.singleMessage = (TextView) convertView.findViewById(R.id.singleMessage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (m.getNickname().equals(Chat.nickname)) {
            holder.singleMessage.setText(String.format("%s: %s", m.getNickname(), m.getMessage()));
        } else {
            holder.singleMessage.setText(String.format("%s (%.2fm): %s", m.getNickname(), m.getDistance(), m.getMessage()));
        }

        return convertView;
    }

    static class ViewHolder {
        public TextView singleMessage;
    }

}
