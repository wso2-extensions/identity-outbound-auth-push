import React from 'react';
import {Image, StyleSheet} from 'react-native';

import {createBottomTabNavigator} from '@react-navigation/bottom-tabs';

import AccountsScreen from './AccountsScreen';
import ActivityScreen from './ActivityScreen';
import AsyncStorage from '@react-native-async-storage/async-storage';

const Tab = createBottomTabNavigator();

const getAccountData = async () => {
    console.log('Get accounts at main');
    return await AsyncStorage.getItem('accounts').then((accounts) => {
        console.log('Accounts from Async at main:' + JSON.stringify(accounts));
        return accounts;
    });
};

const MainScreen = () => {
    return (
        <Tab.Navigator
            screenOptions={({route}) => ({
                tabBarIcon: ({focused, color, size}) => {
                    if (route.name === 'Accounts') {
                        return (
                            <Image
                                source={require('../assets/img/user-profile.png')}
                                style={{tintColor: focused ? '#FD7308' : '#363636'}}
                            />
                        );
                    } else if (route.name === 'Activity') {
                        return (
                            <Image
                                source={require('../assets/img/material-history.png')}
                                style={{tintColor: focused ? '#FD7308' : '#363636'}}
                            />
                        );
                    }
                },
            })}
            tabBarOptions={{
                activeTintColor: '#FD7308',
                inactiveTintColor: '#363636',
                labelStyle: {
                    fontSize: 12,
                    fontFamily: 'Roboto-Medium',
                },
                style: {
                    height: '10%',
                    paddingTop: '2%',
                },
            }}>
            <Tab.Screen name="Accounts" component={AccountsScreen}/>
            <Tab.Screen
                name="Activity"
                component={ActivityScreen}
                // options={{tabBarBadge: 3}}
            />
        </Tab.Navigator>
    );
};

const styles = StyleSheet.create({
    container: {
        marginVertical: '40%',
        alignContent: 'center',
        flexDirection: 'column',
        justifyContent: 'center',
        marginTop: '4%',
    },
    logo: {
        alignSelf: 'center',
        width: '25%',
        resizeMode: 'contain',
    },
    logoText: {
        color: '#f47b20',
        textAlign: 'center',
        fontWeight: '600',
        fontSize: 18,
        top: -30,
        left: 20,
    },
    logoView: {
        flex: 3,
    },
    titleView: {
        flex: 2,
    },
});

export default MainScreen;
