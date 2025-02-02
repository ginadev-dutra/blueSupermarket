package DAO;

import factory.Factory;
import model.Carrinho;
import model.Compra;
import model.Produto;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class CarrinhoDao {
    private Statement stm;
    private Factory f;

    public CarrinhoDao() throws SQLException {
        this.f = new Factory();
        f.setConnection("jdbc:mysql://localhost:3306/bluesupermarket?useTimezone=true&serverTimezone=UTC&useSSL=false");
        this.stm = f.getC().createStatement();
    }

    public Compra inserirCompra(Compra compra){

        String sql = "INSERT INTO compras (idProduto, nomProd, qtn, cpfUsuario, cep, valorFrete, prazoEntrega, dataCompra) VALUES (?,?,?,?,?,?,?,?)";
        try(PreparedStatement pstm = stm.getConnection().prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            pstm.setInt(1,compra.getIdProdutos());
            pstm.setString(2,compra.getNomeProd());
            pstm.setInt(3,compra.getQtn());
            pstm.setString(4,compra.getCpfUsuario());
            pstm.setString(5,compra.getCep());
            pstm.setDouble(6,compra.getValorFrete());
            pstm.setInt(7,compra.getPrazoEntrega());
            pstm.setString(8,compra.getDataCompra());
            pstm.execute();
            try(ResultSet rst = pstm.getGeneratedKeys()) {
                while (rst.next()) {
                    compra.setIdCarrinhos(rst.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.getMessage();
            System.out.println("Não foi possível isnserir compra");
        }
        return compra;
    }
    public void adicionaCarrinho(List<Produto> listProd){

        for (Produto produto:listProd) {
            String sql = "INSERT INTO carrinho (idProd) VALUES (?)";
            try(PreparedStatement pstm = stm.getConnection().prepareStatement(sql)){
                pstm.setInt(1,produto.getIdProd());
                pstm.execute();
            } catch (SQLException e) {
                e.getMessage();
                System.out.println("Não foi possível isnserir produto");
            }
        }
    }
    public List<Carrinho> listaProdutosCarrinho(){
        List<Carrinho> lista = new ArrayList<>();
        String sql = "SELECT idProd FROM carrinho";
        try {
            PreparedStatement ps = this.stm.getConnection().prepareStatement(sql);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            while(rs.next()) {
                lista.add(new Carrinho(rs.getInt("idProd")));
            }
            System.out.println(lista.size());
            return lista;
        }catch(SQLException e) {
            System.out.println("ERRO AO OBTER PRODUTO! (method getProdutos())");
            System.out.println(e.getMessage());
            return null;
        }
    }


    public void deletarCarrinho(int id){
        List<Carrinho> listCarrinho = new ArrayList<>();
        listCarrinho.addAll(listaProdutosCarrinho());
        for (Carrinho carrinho:listCarrinho) {
          if (carrinho.getIdProd() == id){
              String sql = "DELETE FROM carrinho WHERE idCar = ?";
              try(PreparedStatement pstm = stm.getConnection().prepareStatement(sql)){
                  pstm.setInt(1,carrinho.getIdCar());
                  pstm.execute();
                  return;
              } catch (SQLException e) {
                  e.getMessage();
                  System.out.println("Não foi possível isnserir produto");
              }
          }
        }
    }

    public List<Compra> buscaCompraPorData(String dataBusca){
        List<Compra> listaCompraJson = new ArrayList();

        String sql = "SELECT * FROM compras WHERE dataCompra= ?";

        try {
            PreparedStatement ps = this.stm.getConnection().prepareStatement(sql);
//            ps.setString(1,cpf);
            ps.setString(1,dataBusca);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            while(rs.next()) {
                listaCompraJson.add(new Compra(rs.getInt(1),rs.getInt(2),rs.getString(3),rs.getString(6),rs.getDouble(7),rs.getInt(8),rs.getString(9)));
                if(!rs.next()) return listaCompraJson;
            }
            return listaCompraJson;
        }catch(SQLException e) {
            System.out.println("ERRO AO OBTER LISTA DE COMPRA! (method getProdutos())");
            System.out.println(e.getMessage());
            return null;
        }

    }

    public void truncateCarrinho(){
            String sql = "TRUNCATE carrinho";
            try(PreparedStatement pstm = stm.getConnection().prepareStatement(sql)){
                pstm.execute();
            } catch (SQLException e) {
                e.getMessage();
                System.out.println("Produto não deletado");
            }
    }

    public List<Compra> listaUltimaCompra(String cpfUsuario){
        List<Compra> lista = new ArrayList<>();
        String sql = " SELECT * FROM compras  JOIN (SELECT cpfUsuario, MAX(dataCompra) ultimaData from compras where cpfUsuario = ?)ultimoRegistro\n" +
                "                     on compras.dataCompra = ultimoRegistro.ultimaData\n" +
                "                     and compras.cpfUsuario = ultimoRegistro.cpfUsuario;";
        try {

            PreparedStatement ps = this.stm.getConnection().prepareStatement(sql);
            ps.setString(1,cpfUsuario);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            while(rs.next()) {
                lista.add(new Compra(rs.getInt("idCarrinhos"),
                        rs.getInt("idProduto"),
                        rs.getString("nomProd"),
                        rs.getInt("qtn"),
                        rs.getString("cpfUsuario"),
                        rs.getString("cep"),
                        rs.getDouble("valorFrete"),
                        rs.getInt("prazoEntrega"),
                        0,
                        rs.getString("dataCompra")));
            }
            return lista;
        }catch(SQLException e) {
            System.out.println("ERRO AO ENCONTRAR ULTIMA COMPRA! (method listaUltimaCompra())");
            System.out.println(e.getMessage());
            return null;
        }
    }

}
