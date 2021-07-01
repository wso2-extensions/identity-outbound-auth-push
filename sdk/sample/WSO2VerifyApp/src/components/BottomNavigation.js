/**
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import React from 'react';
import {View, StyleSheet, Text, Image} from 'react-native';

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
