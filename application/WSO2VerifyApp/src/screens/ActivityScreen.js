import React, {useState, useEffect} from 'react';
import {
    View,
    Image,
    Text,
    StyleSheet,
    FlatList,
} from 'react-native';
import ActivityCard from '../components/ActivityCard';
import {
    heightPercentageToDP as hp,
} from 'react-native-responsive-screen';
import AsyncStorage from '@react-native-async-storage/async-storage';

const ActivityScreen = ({navigation}) => {
    const [selectedMenu, setSelectedMenu] = useState('Accepted');
    const [data, setData] = useState([]);

    /*
     * Loading the data when the accounts screen is focussed to be populated in the cards
     */
    useEffect(() => {
        const unsubscribe = navigation.addListener('focus', () => {
            const getData = async () => {
                await AsyncStorage.getItem('activity').then((activity) => {
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

    const renderItem = ({item}) => {
        return <ActivityCard account={item}/>;
    };

    let selectData = filterData();

    return (
        <View
            style={{
                flexDirection: 'column',
                height: hp('97%'),
                justifyContent: 'center',
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
                        }}
                        onPress={() => {
                            setSelectedMenu('Accepted');
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
                    keyExtractor={(item) => item.activityId.toString()}
                    extraData={selectedMenu}
                    style={styles.accountsList}
                />
            </View>
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
