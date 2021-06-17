import React, {useState, useEffect} from 'react';
import {
  View,
  Image,
  Text,
  StyleSheet,
  FlatList,
  TouchableOpacity,
} from 'react-native';
import ActivityCard from '../components/ActivityCard';
import BottomNavigation from '../components/BottomNavigation';
import {
  widthPercentageToDP as wp,
  heightPercentageToDP as hp,
} from 'react-native-responsive-screen';
import AsyncStorage from '@react-native-async-storage/async-storage';

// const data = [
//   {
//     accountId: 1,
//     username: 'kushanb@wso2.com',
//     displayName: 'Kushan Bhareti',
//     organization: 'WSO2',
//     status: 'Declined',
//   },
//   {
//     accountId: 2,
//     username: 'blue@wso2.com',
//     displayName: 'Blue Hudsen',
//     organization: 'WSO2',
//     status: 'Accepted',
//     time: 'Today | 3.30 p.m.',
//   },
//   {
//     accountId: 3,
//     username: 'jonathan@wso2.com',
//     displayName: 'Jonathan Swiss',
//     organization: 'WSO2',
//     status: 'Missed',
//   },
//   {
//     accountId: 4,
//     username: 'nike@google.com',
//     displayName: 'Nike Crane',
//     organization: 'Google',
//     device: 'Phone',
//     status: 'Accepted',
//   },
// ];

const ActivityScreen = ({navigation}) => {
  const [selectedMenu, setSelectedMenu] = useState('Accepted');
  const [data, setData] = useState([]);

  // const [filteredData, setFilteredData] = useState(0);

  /*
   * Loading the data when the accounts screen is focussed to be populated in the cards
   */
  useEffect(() => {
    const unsubscribe = navigation.addListener('focus', () => {
      const getData = async () => {
        await AsyncStorage.getItem('activity').then((activity) => {
          // console.log(JSON.stringify(data) + ' and ' + activity);
          if (activity != JSON.stringify(data)) {
            setData(JSON.parse(activity));
            console.log('Changed so set');
          } else {
            console.log('Always running issue!');
          }
        });
      };
      getData();
    });
    return unsubscribe;
  }, [navigation]);

  const filterData = () => {
    console.log('Filter data called');
    console.log(selectedMenu);
    console.log('Activity data: ' + JSON.stringify(data));
    return data
      ? data.filter((item) => item.authenticationStatus == selectedMenu)
      : [];
  };

  // if (selectedMenu == '') {
  //   setSelectedMenu('Allowed');
  //   setFilteredData(filterData());
  //   console.log('Status: ' + selectedMenu + filteredData);
  // }

  const renderItem = ({item}) => {
    return <ActivityCard account={item} />;
  };

  let selectData = filterData();
  // console.log('Filtered Data: ' + JSON.stringify(selectData));

  return (
    <View
      style={{
        flexDirection: 'column',
        height: hp('97%'),
        justifyContent: 'center',
        // marginTop: '3%',
        backgroundColor: '#FFF',
      }}>
      <View style={styles.logoView}>
        <Image
          source={require('../assets/img/wso2logo.png')}
          style={styles.logo}
        />
      </View>
      <View style={styles.titleView}>
        <Text style={styles.title}>Activity</Text>
      </View>
      <View style={{flex: 13, flexDirection: 'column'}}>
        <View
          style={{
            flexDirection: 'row',
            justifyContent: 'space-evenly',
          }}>
          <Text
            style={{
              fontSize: 20,
              fontFamily:
                selectedMenu === 'Accepted' ? 'Roboto-Regular' : 'Roboto-Light',
              // borderBottomColor: '#FA668A',
              // borderBottomWidth: 3,
            }}
            onPress={() => {
              setSelectedMenu('Accepted');
              // setFilteredData(filterData(data));
              console.log('Accepted Pressed');
              console.log(selectedMenu);
            }}>
            Accepted
          </Text>
          <Text
            style={{
              fontSize: 20,
              fontFamily:
                selectedMenu === 'Denied' ? 'Roboto-Regular' : 'Roboto-Light',
            }}
            onPress={() => {
              setSelectedMenu('Denied');
              // setFilteredData(filterData(data));
            }}>
            Denied
          </Text>
          <Text
            style={{
              fontSize: 20,
              fontFamily:
                selectedMenu === 'Missed' ? 'Roboto-Regular' : 'Roboto-Light',
            }}
            onPress={() => {
              setSelectedMenu('Missed');
              // setFilteredData(filterData(data));
            }}>
            Missed
          </Text>
        </View>
        <View
          style={{
            borderBottomColor: 'black',
            borderBottomWidth: 1,
            width: '80%',
            alignSelf: 'center',
            marginVertical: 5,
          }}
        />
        <FlatList
          data={selectData}
          renderItem={renderItem}
          inverted={true}
          // initialScrollIndex={1}
          keyExtractor={(item) => item.activityId.toString()}
          extraData={selectedMenu}
          style={styles.accountsList}
        />
      </View>

      {/* <View style={{flex: 2, justifyContent: 'flex-start'}}>
        <BottomNavigation screen="Activity" />
      </View> */}
    </View>
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
    width: '20%',
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
  title: {
    fontSize: 36,
    // fontWeight: '300',
    fontFamily: 'Roboto-Light',
    textAlign: 'center',
  },
  titleView: {
    flex: 2,
  },
  accountsList: {
    flex: 1,
  },
});

export default ActivityScreen;
