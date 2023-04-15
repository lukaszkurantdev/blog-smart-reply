import React, {useEffect, useState} from 'react';
import {SafeAreaView, StyleSheet, Text, View} from 'react-native';
import SmartReplyModule, {Message} from './SmartReply';

const MESSAGES: Message[] = [
  {
    id: 1,
    text: 'Hello',
    userId: 'test',
    isLocalUser: false,
  },
  {
    id: 2,
    text: 'When will you be in the office?',
    userId: 'test',
    isLocalUser: false,
  },
];

function App(): JSX.Element {
  const [suggestions, setSuggestions] = useState<string[]>([]);

  const getSuggestions = async (messages: Message[]) => {
    try {
      const result = await SmartReplyModule.getSmartReply(messages);
      setSuggestions(result);
    } catch (error) {
      // error
    }
  };

  useEffect(() => {
    getSuggestions(MESSAGES);
  }, []);

  return (
    <SafeAreaView>
      <View style={styles.container}>
        {MESSAGES.map(message => (
          <Text key={message.id} style={styles.message}>
            {message.text}
          </Text>
        ))}

        <Text style={styles.title}>Suggestions:</Text>
        <View style={styles.suggestionsContainer}>
          {suggestions.map((suggestion, index) => (
            <Text key={index} style={styles.suggestion}>
              {suggestion}
            </Text>
          ))}
        </View>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 25,
  },
  message: {
    backgroundColor: 'lightgray',
    padding: 10,
    marginVertical: 5,
  },
  suggestionsContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    columnGap: 20,
  },
  suggestion: {
    backgroundColor: 'gray',
    padding: 10,
  },
  title: {
    marginTop: 32,
    marginBottom: 10,
  },
});

export default App;
