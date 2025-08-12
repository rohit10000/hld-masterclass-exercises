[Aim: 2 phase commit]
    Have 2 controller classes:
        FoodController:
            /food/reserve
                foodService.reserve(foodId)
            /food/book
                foodService.book(orderId, foodId)
            
        AgentController:
            /agent/reserve
               agentService.reserve();

            /agent/book
                agentService.book(orderId);

    Have 2 Services:
        AgentService
            reserve():
                sql transactions
                begin();
                row, error = select * from agents where is_reserved = false and order_id = null for update;
                if (error != null)
                    rollback();
                Agent = row
                
                _, err = update agents set is_reserved = true where id = agent.id;
                if (err != null)
                    rollback();
                commit();

            book(orderId):
                sql transactions
                begin();
                row, error = select * from agents where is_reserved = true and order_id = null for update;
                if (error != null)
                    rollback();
                Agent = row
                _, err = update agents set is_reserved = false, order_id = orderId;
                if (err != null)
                    rollback();
                commit();
                
        FoodService
            reserve(foodId):
                begin();
                row, err = select * from packets where food_id = foodId and is_reserved = false and order_id = null for update;
                if (error != null)
                    rollback();
                Packet = row
                _, err = update packets set is_reserved = true where id = packet.id;
                if (error != null)
                    rollback();
                commit();
            book(foodId, orderId):
                begin();
                row, err = select * from packets where food_id = foodId and is_reserved = true and order_id = null for update;
                if (error != null)
                    rollback();
                Packet = row
                _, err = udpate packets set is_reserved = false and order_id = orderId where packet_id = packet.id;
                if (error != null)
                    rollback();
                commit();

dto:
    Agent: id, is_reserved(boolean), order_id
    Food: id, name
    Packet: id, food_id, is_reserved, order_id

Database tables

agents

id | is_reserved | order_id

packets
id | food_id (fk) | is_reserved | order_id

foods
id | name

Feel free to add repository layer.
The database i will use is mysql database:
with the following credentials:
    host: localhost, port: 3306, u - root, p - rohitsingh, database - hertever