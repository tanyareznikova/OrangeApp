package com.example.orange_app.adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.orange_app.ChatsFragment;
import com.example.orange_app.ContactsFragment;
import com.example.orange_app.GroupsFragment;
import com.example.orange_app.RequestsFragment;

public class TabsAccessorAdapter extends FragmentPagerAdapter {

    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
            case 1:
                GroupsFragment groupsFragment = new GroupsFragment();
                return groupsFragment;
            case 2:
                ContactsFragment contactsFragment = new ContactsFragment();
                return contactsFragment;
            case 3:
                RequestsFragment requestsFragment = new RequestsFragment();
                return requestsFragment;
            default:
                return null;
        }//switch

    }//Fragment getItem

    @Override
    public int getCount() {
        return 4;
    }//int getCount

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Чаты";
            case 1:
                return "Группы";
            case 2:
                return "Контакты";
            case 3:
                return "Запросы";
            default:
                return null;
        }//switch
    }
}//TabsAccessorAdapter
