import {NativeModules} from 'react-native';

const {SmartReplyModule} = NativeModules;

export type Message = {
  id: number;
  text: string;
  userId: string;
  isLocalUser: boolean;
};

interface SmartReplyInterface {
  getSmartReply(messages: Message[]): Promise<string[]>;
}

export default SmartReplyModule as SmartReplyInterface;
