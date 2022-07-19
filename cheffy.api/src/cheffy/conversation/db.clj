(ns cheffy.conversation.db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

(defmulti dispatch (fn [[name _db _data]] name))

(defmethod dispatch :find-conversation-by-uid
  [[_ db {:keys [uid]}]]
  (with-open [conn (jdbc/get-connection db)]
    (let [conn-opts (jdbc/with-options conn (:options db))
          conversations (sql/find-by-keys conn-opts :conversation uid)]
      (doall
        ; The for function is lazy so to execute the queries we need to wrap it with doall function
        (for [{:conversation/keys [conversation-id] :as conversation} conversations
              :let [{:message/keys [created-at]} (jdbc/execute-one! conn-opts ["SELECT created_at FROM message
                                                                                WHERE conversation_id = ?
                                                                                ORDER BY created_at DESC
                                                                                LIMIT 1" conversation-id])
                    with (jdbc/execute-one! conn-opts ["SELECT uid FROM conversation
                                                        WHERE uid != ? AND conversation_id = ?" uid conversation-id])
                    [{:account/keys [name picture]}] (sql/find-by-keys conn-opts :account with)]]
          (assoc conversation
            :conversation/updated-at created-at
            :conversation/with-name name
            :conversation/with-picture picture))))))

(comment
  (dispatch [:find-conversation-by-uid {} {}]))