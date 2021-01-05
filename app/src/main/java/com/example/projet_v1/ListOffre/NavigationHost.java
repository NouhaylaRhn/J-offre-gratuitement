package com.example.projet_v1.ListOffre;

import androidx.fragment.app.Fragment;


public interface NavigationHost {

    void navigateTo(Fragment fragment, boolean addToBackstack);
}
