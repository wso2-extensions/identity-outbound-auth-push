import React from 'react';
import {View, StyleSheet, Text, Image, TouchableOpacity} from 'react-native';

const BottomNavigation = ({screen}) => {
  return (
    <View style={styles.navContainer}>
      <View style={styles.menuItemView}>
        <Image
          source={require('../assets/img/user-profile.png')}
          style={screen === 'Accounts' ? styles.selectedMenuImage : null}
        />
        <Text style={screen === 'Accounts' ? styles.selectedMenuText : null}>
          Accounts
        </Text>
      </View>
      <View style={styles.menuItemView}>
        <Image
          source={require('../assets/img/material-history.png')}
          style={screen === 'Activity' ? styles.selectedMenuImage : null}
        />
        <Text style={screen === 'Activity' ? styles.selectedMenuText : null}>
          Activity
        </Text>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  navContainer: {
    backgroundColor: '#FFF',
    width: '100%',
    paddingVertical: '4%',
    paddingHorizontal: '20%',
    position: 'absolute',
    shadowColor: 'black',
    shadowOffset: {width: 0, height: 2},
    shadowOpacity: 0.2,
    shadowRadius: 8,
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginTop: '1%',
  },
  menuItemView: {
    alignItems: 'center',
  },
  selectedMenuImage: {
    tintColor: '#FD7308',
  },
  selectedMenuText: {
    color: '#FD7308',
  },
});

export default BottomNavigation;
