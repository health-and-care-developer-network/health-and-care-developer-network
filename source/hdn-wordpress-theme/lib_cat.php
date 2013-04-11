 <?php
/*
Template Name: Library Category
*/
?>

<?php get_header(); ?>
		
		<div role="main" class="main">
			<div class="page_title">
				<div class="wrapper">
					<hgroup>
						<h1>Library</h1>
						<h2>Browse and search developer information</h2>
					</hgroup>
				</div>
			</div><!--end page title-->
			
			<div class="main_content cf">
				<?php include_once "breadcrumbs.php"; ?>
				
				<div class="wrapper cf container">
					
					<div class="one_quarter tree_wrap">
						<?php include_once "tree_nav.php"; ?>
					</div>
					
					<div class="three_quarters">
						
						<div class="content_wrap cf">
							<?php if ( have_posts() ) : while ( have_posts() ) : the_post(); ?>
								<h2 class="underline"><?php the_title(); ?></h2>
							<?php endwhile; endif; ?>
							
							
							<?php
								$this_page_id=$wp_query->post->ID;
								query_posts( array( 'post_type' => 'library', 'pagetype' => 'article',  'orderby' => 'title', 'order' => 'asc', 'post_parent' => $this_page_id, 'depth' => '1', ) ); 
								if ( have_posts() ) : while ( have_posts() ) : the_post(); 
							?>
							
							<div class="half">
								<article class="lib_list">
									<a href="<?php the_permalink(); ?>"><h3 class="underline"><?php the_title(); ?><span class="link_arrow" aria-hidden="true" data-icon="&#x28;"></span></h3></a>
									<?php the_excerpt(); ?>
								</article>
							</div>
							
							<?php endwhile; ?>

							
							<?php endif; wp_reset_query();?>	
						</div><!--end content wrap-->
						
						<div class="clear"></div>
						
						<div class="content_wrap cf">
							<?php
								$this_page_id=$wp_query->post->ID;
								query_posts( array( 'post_type' => 'library', 'pagetype' => 'sub-category',  'orderby' => 'title', 'order' => 'asc', 'post_parent' => $this_page_id, 'depth' => '1', ) ); 
								if ( have_posts() ) : while ( have_posts() ) : the_post(); 
							?>
							

							<div class="half">
								<a href="<?php the_permalink(); ?>" class="box">
									<div class="line green_line"></div>
									<section class="nav_box cat_box green">
										<h2><?php the_title(); ?></h2>
										<p><?php the_excerpt(); ?>
									</section>
								</a>
							</div>
							
							<?php endwhile; ?>

							
							<?php endif; wp_reset_query();?>
						</div><!--end content wrap-->
						
					</div><!--end two thirds-->
					
				</div><!--end wrapper-->
			</div><!--end main_content-->
		</div><!-- end main -->
		
<?php get_footer(); ?>	